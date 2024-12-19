/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Sets;

import org.wcardinal.controller.ControllerAttributes;
import org.wcardinal.controller.ControllerContext;
import org.wcardinal.controller.ControllerContextAware;
import org.wcardinal.controller.ControllerIo;
import org.wcardinal.controller.Factory;
import org.wcardinal.controller.TimeoutFuture;
import org.wcardinal.controller.TriggerErrors;
import org.wcardinal.controller.TriggerResult;
import org.wcardinal.controller.data.SMap.Update;
import org.wcardinal.controller.data.internal.SBase;
import org.wcardinal.controller.data.internal.SChange;
import org.wcardinal.controller.data.internal.SClassImpl;
import org.wcardinal.controller.data.internal.SContainer;
import org.wcardinal.controller.data.internal.SContainers;
import org.wcardinal.controller.data.internal.SMapImpl;
import org.wcardinal.controller.data.internal.SNavigableMapImpl;
import org.wcardinal.controller.data.internal.SParent;
import org.wcardinal.controller.data.internal.SData;
import org.wcardinal.controller.data.internal.SStringImpl;
import org.wcardinal.controller.internal.info.SetDynamicDataMap;
import org.wcardinal.controller.internal.info.SetDynamicInfo;
import org.wcardinal.controller.internal.info.SetDynamicInfoMap;
import org.wcardinal.controller.internal.info.AckInfo;
import org.wcardinal.controller.internal.info.DynamicDataJsonNode;
import org.wcardinal.controller.internal.info.DynamicDataObject;
import org.wcardinal.controller.internal.info.DynamicInfo;
import org.wcardinal.controller.internal.info.OnIdleCheckMethodInfo;
import org.wcardinal.controller.internal.info.SenderIdAndDynamicInfo;
import org.wcardinal.controller.internal.info.ReceivedDynamicInfo;
import org.wcardinal.controller.internal.info.SChangeInfo;
import org.wcardinal.controller.internal.info.StaticData;
import org.wcardinal.controller.internal.info.StaticInfo;
import org.wcardinal.controller.internal.info.StaticInstanceInfo;
import org.wcardinal.controller.internal.info.StaticDataTask;
import org.wcardinal.util.Reference;
import org.wcardinal.util.json.Json;
import org.wcardinal.util.reflection.AbstractMethods;
import org.wcardinal.util.reflection.AbstractTypedMethods;
import org.wcardinal.util.reflection.CallableMethods;
import org.wcardinal.util.reflection.ExceptionHandlerMethod;
import org.wcardinal.util.reflection.LockRequirement;
import org.wcardinal.util.reflection.LockRequirements;
import org.wcardinal.util.reflection.MethodAndOrders;
import org.wcardinal.util.reflection.MethodContainer;
import org.wcardinal.util.reflection.MethodResult;
import org.wcardinal.util.reflection.MethodResultData;
import org.wcardinal.util.reflection.MethodResultException;
import org.wcardinal.util.reflection.MethodResultVoid;
import org.wcardinal.util.reflection.TrackingData;
import org.wcardinal.util.reflection.TrackingIds;
import org.wcardinal.util.reflection.TypedExceptionHandlerMethods;
import org.wcardinal.util.reflection.VoidParametrizedMethods;
import org.wcardinal.util.reflection.VoidTypedParametrizedMethods;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Scheduler;
import org.wcardinal.util.thread.Unlocker;

public class Controller implements ControllerContext, MethodContainer, SParent {
	static final Logger logger = LoggerFactory.getLogger(Controller.class);

	final Set<Controller> parents = Sets.newConcurrentHashSet();
	final ControllerFactory factory;
	final Object instance;
	final String name;
	final ControllerBaggage baggage;

	static final String PAGE_ACTIVE_ID = "$pa";
	final SStringImpl pageActive;

	static final String CALL_REQUESTS_ID = "$cq";
	static final String CALL_RESULTS_ID = "$cr";
	final Map<SParent, CallSMaps> originToCallSMaps = new HashMap<>();

	final int MAXIMUM_TRIGGER_QUEUE_SIZE;
	static final String TRIGGER_REQUESTS_ID = "$tq";
	static final String TRIGGER_RESULTS_ID = "$tr";
	final String triggerIdPrefix;
	final AtomicLong _triggerId = new AtomicLong( 0 );
	final SNavigableMapImpl<TriggerRequest> triggerRequests;
	final Map<SParent, SMapImpl<List<JsonNode>>> originToTriggerResults = new HashMap<>();

	static final String TRIGGER_DIRECTS_ID = "$td";
	final Map<SParent, SClassImpl<List<TriggerRequest>>> originToTriggerDirects = new HashMap<>();

	final Map<String, Controller> controllerNameToController;

	final Map<String, SBase<?>> sbaseNameToSBase;
	final Set<SContainer<?, ?>> scontainers;
	final Map<SParent, Map<String, SData>> originToSData = new HashMap<>();

	final AutoCloseableReentrantLock lock;
	final Unlocker unlocker;

	boolean isChanged;

	final AtomicLong timeId = new AtomicLong(0);
	final ConcurrentHashMap<Long, ConcurrentData> times = new ConcurrentHashMap<>();
	final ThreadLocal<Long> timeIdLocal = new ThreadLocal<>();

	final ThreadLocal<TrackingIds> trackingIdsLocal = new ThreadLocal<>();

	final AtomicBoolean visibility;

	final ArrayNode factoryParameters;

	final Map<Object, Object> workingDatas = new ConcurrentHashMap<>();

	final TaskInternalQueue tasks;
	final Map<String, ControllerOnChangeHandler> onChangeHandlers = new HashMap<>();
	final Set<ControllerDynamicInfoHandler> dynamicInfoHandlers = new HashSet<>();
	final ThreadLocal<TaskInternal> task = new ThreadLocal<>();

	public Controller( final String name, final Controller parent, final ControllerFactory factory,
			final Object instance, final ControllerBaggage baggage, final ArrayNode factoryParameters,
			final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks ){
		this.factory = factory;
		this.instance = instance;
		this.name = name;
		this.isChanged = false;
		this.baggage = baggage;
		this.visibility = new AtomicBoolean( true );
		this.factoryParameters = (factoryParameters != null ? factoryParameters : Json.mapper.createArrayNode());
		this.lock = lock;
		this.unlocker = new Unlocker( this );
		this.tasks = tasks;
		this.MAXIMUM_TRIGGER_QUEUE_SIZE = baggage.configuration.getMaximumTriggerQueueSize();

		sbaseNameToSBase = setSScalarFields();
		scontainers = setSContainerFields();

		pageActive = new SStringImpl( factory.pagePrimary );
		pageActive.setParent(this);
		pageActive.setLock(lock);
		put( PAGE_ACTIVE_ID, pageActive );

		triggerIdPrefix = Long.toString( System.currentTimeMillis(), 32 ) + Long.toString( Math.round(Math.random() * 1024), 32 );
		triggerRequests = new SNavigableMapImpl<TriggerRequest>();
		triggerRequests.init(TRIGGER_REQUESTS_ID, this, lock, ResolvableType.forClass(TriggerRequest.class), Properties.empty() );
		triggerRequests.setAscendingComparator();
		scontainers.add(triggerRequests);

		createTasks();

		createAdditionals();

		addParent( parent );

		controllerNameToController = setControllerFields();

		// Facade
		for( final Map.Entry<Field, Field> entry: factory.facades.entrySet() ){
			final Field facadeField = entry.getKey();
			final Field controllerContextField = entry.getValue();
			try {
				controllerContextField.set(facadeField.get(instance), this);
			} catch ( final Exception e ){
				logInitializationError( facadeField, e );
			}
		}

		// ControllerContext
		for( final Field controllerContext: factory.controllerContexts ){
			try {
				controllerContext.set(instance, this);
			} catch ( final Exception e ){
				logInitializationError( this, e );
			}
		}

		// ControllerContextAware
		if( instance instanceof ControllerContextAware ){
			((ControllerContextAware)instance).setControllerContext(this);
		}

		// Working data
		factory.callables.init( this );
		factory.callableExceptionHandlers.init( this );
		factory.tasks.init( this );
		factory.taskExceptionHandlers.init( this );
		factory.onChangeMethods.init( this );
		factory.onHideMethods.init( this );
		factory.onNoticeMethods.init( this );
		factory.onTimeMethods.init( this );
		factory.onShowMethods.init( this );
	}

	@Override
	public <T> void handle( final MethodResult<T> result ) {
		if( result instanceof MethodResultException ) {
			final MethodResultException<?> resultException = (MethodResultException<?>) result;
			final Method method = resultException.getMethod();
			final Object[] parameters = resultException.getParameters();
			final Throwable throwable = resultException.getInvocationTargetThrowable();
			if( throwable != null ) {
				if( handle( this, method.getName(), throwable.getClass(), throwable, method, parameters ) != true ) {
					logger.error( "Unhandled exception raised from the method '{}'", method, throwable );
				}
			} else {
				final Exception exception = resultException.getException();
				if( handle( this, method.getName(), exception.getClass(), exception, method, parameters ) != true ) {
					if( exception instanceof IllegalArgumentException ) {
						logger.error( "Illegal arguments to the method '{}'", method, throwable );
					} else {
						logger.error( "Failed to call the method '{}'", method, throwable );
					}
				}
			}
		}
	}

	boolean handle( final Controller controller, final String name, final Class<? extends Throwable> throwableClass, final Throwable throwable, final Method method, final Object[] parameters ) {
		final TypedExceptionHandlerMethods<Void> handlers = controller.factory.exceptionHandlers;
		final ExceptionHandlerMethod<Void> handler = handlers.find( name, throwableClass );
		if( handler != null ) {
			final TrackingData handlerTrackingData = handlers.getTrackingData( name, controller, controller.instance );
			if( LockRequirements.REQUIRED_OR_UNSPECIFIED.contains( handler.getLockRequirement() ) ) {
				if( controller.isLockedByCurrentThread() ) {
					handler.call( controller, handlerTrackingData, null, controller.instance, throwable, method );
				} else {
					try( final Unlocker unlocker = controller.lock() ) {
						handler.call( controller, handlerTrackingData, null, controller.instance, throwable, method );
					}
				}
			} else {
				if( controller.isLockedByCurrentThread() ) {
					controller.baggage.scheduler.execute(new Runnable(){
						@Override
						public void run() {
							handler.call( controller, handlerTrackingData, null, controller.instance, throwable, method );
						}
					});
				} else {
					handler.call( controller, handlerTrackingData, null, controller.instance, throwable, method );
				}
			}
			return true;
		}

		if( (controller instanceof SharedComponentController) != true ) {
			final String newName = controller.getName()+"."+name;
			for( final Controller parent: controller.parents ) {
				if( handle( parent, newName, throwableClass, throwable, method, parameters ) ) {
					return true;
				}
			}
		}

		return false;
	}

	void createAdditionals() {
		// DO NOTHING
	}

	void createTasks(){
		for( final String name: factory.tasks.getTypes() ){
			final StaticData staticData = factory.staticInfo.nameToData.get( name );
			if( staticData instanceof StaticDataTask ) {
				new TaskController( name, this, tasks, (StaticDataTask) staticData );
			}
		}
	}

	public void addParent( final Controller parent ){
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			if( parent == null || parents.add( parent ) ) {
				// SBase
				final Map<String, SData> nameToSData = new HashMap<String, SData>();
				for( final Map.Entry<String, SBase<?>> entry: sbaseNameToSBase.entrySet() ) {
					final String name = entry.getKey();
					final SBase<?> sbase = entry.getValue();
					nameToSData.put( name, new SData( sbase ) );
				}
				originToSData.put(parent, nameToSData);

				// Call SMaps
				originToCallSMaps.put( parent, new CallSMapsImpl( parent, this, this.lock, CALL_REQUESTS_ID, CALL_RESULTS_ID ) );

				// Trigger results
				final SMapImpl<List<JsonNode>> triggerResults = new SMapImpl<>();
				triggerResults.init(TRIGGER_RESULTS_ID, this, this.lock, ResolvableType.forClassWithGenerics(List.class, JsonNode.class), Properties.empty() );
				triggerResults.addOrigin( parent );
				originToTriggerResults.put( parent, triggerResults );

				// Trigger directs
				final SClassImpl<List<TriggerRequest>> triggerDirects = new SClassImpl<List<TriggerRequest>>();
				triggerDirects.setParent( this );
				triggerDirects.setLock( this.lock );
				triggerDirects.setReadOnly( true );
				triggerDirects.setWeak( true );
				triggerDirects.setGenericType( ResolvableType.forClassWithGenerics(List.class, TriggerRequest.class) );
				nameToSData.put( TRIGGER_DIRECTS_ID, new SData( triggerDirects ) );
				originToTriggerDirects.put( parent, triggerDirects );

				// SContainer
				for( final SContainer<?, ?> scontainer: scontainers ) {
					scontainer.addOrigin( parent );
				}
			}
		}
	}

	void removeParent( final Controller parent ){
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			parents.remove( parent );
			originToSData.remove( parent );

			// Callable
			final CallSMaps callSMaps = originToCallSMaps.remove( parent );
			if( callSMaps != null ) {
				callSMaps.removeOrigin( parent );
			}

			// Trigger
			final SMapImpl<List<JsonNode>> triggerResults = originToTriggerResults.remove( parent );
			if( triggerResults != null ) {
				triggerResults.removeOrigin( parent );
			}

			// Trigger directs
			originToTriggerDirects.remove( parent );

			// SContainer
			for( final SContainer<?, ?> scontainer: scontainers ) {
				scontainer.removeOrigin( parent );
			}
		}
	}

	void setAuthorizedRevisionData( final Controller origin, final AckInfo info, final boolean allowCompaction ){
		final Map<String, SData> nameToSData = originToSData.get( origin );
		if( nameToSData != null ) {
			for( final SData sdata: nameToSData.values() ){
				if( 0 <= sdata.getSentSenderId() && info.contains( sdata.getSentSenderId() ) ) {
					sdata.resetSentSenderId();

					final long authorizedRevision = sdata.getSentRevision();
					sdata.setAuthorizedRevision( authorizedRevision );

					final SBase<?> sbase = sdata.get();
					sbase.onAuthorized( authorizedRevision );
					if( allowCompaction && sbase.isSoft() ) {
						sbase.compact( authorizedRevision );
					}
				}
			}
		}
	}

	void setAuthorizedRevision( final AckInfo info, final boolean allowCompaction, final Map<String, ? extends Controller> nameToController ){
		for( final Controller controller: nameToController.values() ){
			if( controller != null ){
				controller.setAuthorizedRevision( this, info, allowCompaction );
			}
		}
	}

	public void setAuthorizedRevision( final Controller origin, final AckInfo info, final boolean allowCompaction ){
		if( info != null ) {
			try( final Unlocker unlocker = lock() ) {
				setAuthorizedRevisionData( origin, info, allowCompaction );
				setAuthorizedRevision( info, allowCompaction, controllerNameToController );
			}
		}
	}

	boolean isChanged( final SBase<?> data, final SData sdata ){
		return (
			data != null &&
			data.isInitialized() &&
			sdata.getAuthorizedRevision() < data.getRevision()
		);
	}

	boolean isChanged( final SBase<?> data, final SData sdata, final long now, final long timeout ){
		if( isChanged( data, sdata ) ) {
			if( sdata.getSenderId() < 0 ) {
				return sdata.getSentRevision() < data.getRevision() || timeout <= now - sdata.getLastSendTime();
			} else {
				return sdata.getSendingRevision() < data.getRevision();
			}
		}
		return false;
	}

	SenderIdAndDynamicInfo getPartialDynamicInfo( final Controller origin, final Controller target ) {
		for( final Controller parent: parents ) {
			return parent.getPartialDynamicInfo( origin, target );
		}
		return null;
	}

	Map<String, DynamicDataObject> lockDynamicInfoData( final Controller origin, final long senderId, final long now, final long timeout ){
		Map<String, DynamicDataObject> result = null;

		final Map<String, SData> nameToSData = originToSData.get( origin );
		if( nameToSData != null ) {
			final Iterator<Map.Entry<String, SData>> itr = nameToSData.entrySet().iterator();
			while( itr.hasNext() ){
				final Map.Entry<String, SData> entry = itr.next();
				final String name = entry.getKey();
				final SData sdata = entry.getValue();
				final SBase<?> sbase = sdata.get();
				if( isChanged( sbase, sdata, now, timeout ) ) {
					if( result == null ) {
						result = new HashMap<>();
					}
					result.put( name, new DynamicDataObject( sbase.getRevision(), sbase.getType(), sbase.pack( sdata ) ) );
					sdata.lock( senderId, sbase.getRevision() );
				}
			}

			if( result != null ) {
				for( final ControllerDynamicInfoHandler handler: dynamicInfoHandlers ) {
					handler.handle( origin, nameToSData, result, senderId );
				}
			}
		}

		return result;
	}

	Map<String, DynamicInfo> lockDynamicInfo( final Map<String, ? extends Controller> nameToController, final long senderId, final long now, final long timeout ){
		Map<String, DynamicInfo> result = null;

		for( final Map.Entry<String, ? extends Controller> entry: nameToController.entrySet() ){
			final String name = entry.getKey();
			final Controller controller = entry.getValue();
			final DynamicInfo info = controller.lockDynamicInfo( this, senderId, now, timeout );
			if( info != null ) {
				if( result == null ) {
					result = new HashMap<>();
				}
				result.put(name, info);
			}
		}

		return result;
	}

	public DynamicInfo lockDynamicInfo( final Controller origin, final long senderId, final long now, final long timeout ) {
		try( final Unlocker unlocker = lock() ) {
			return DynamicInfo.create(
				lockDynamicInfoData( origin, senderId, now, timeout ),
				lockDynamicInfo( controllerNameToController, senderId, now, timeout )
			);
		}
	}

	void unlockDynamicInfoData( final Controller origin, final long senderId, final long now ){
		final Map<String, SData> nameToSData = originToSData.get( origin );
		if( nameToSData != null ) {
			final Iterator<Map.Entry<String, SData>> itr = nameToSData.entrySet().iterator();
			while( itr.hasNext() ){
				final Map.Entry<String, SData> entry = itr.next();
				final SData sdata = entry.getValue();
				if( sdata.getSenderId() == senderId ) {
					sdata.unlock( now );
				}
			}
		}
	}

	void unlockDynamicInfo( final Map<String, ? extends Controller> nameToController, final long senderId, final long now ){
		for( final Controller controller: nameToController.values() ){
			controller.unlockDynamicInfo( this, senderId, now );
		}
	}

	public void unlockDynamicInfo( final Controller origin, final long senderId, final long now ) {
		try( final Unlocker unlocker = lock() ) {
			unlockDynamicInfoData( origin, senderId, now );
			unlockDynamicInfo( controllerNameToController, senderId, now );
		}
	}

	boolean hasDynamicInfoData( final Controller origin, final long now, final long timeout ){
		final Map<String, SData> nameToSData = originToSData.get( origin );
		if( nameToSData != null ) {
			final Iterator<Map.Entry<String, SData>> itr = nameToSData.entrySet().iterator();
			while( itr.hasNext() ){
				final Map.Entry<String, SData> entry = itr.next();
				final SData sdata = entry.getValue();
				final SBase<?> sbase = sdata.get();
				if( isChanged( sbase, sdata, now, timeout ) ) return true;
			}
		}

		return false;
	}

	boolean hasDynamicInfo( final Map<String, ? extends Controller> nameToController, final long now, final long timeout  ){
		for( final Map.Entry<String, ? extends Controller> entry: nameToController.entrySet() ){
			final Controller controller = entry.getValue();
			if( controller.hasDynamicInfo( this, now, timeout ) ) return true;
		}

		return false;
	}

	boolean hasDynamicInfo( final Controller origin, final long now, final long timeout ) {
		try( final Unlocker unlocker = lock() ) {
			return (
				hasDynamicInfoData( origin, now, timeout ) ||
				hasDynamicInfo( controllerNameToController, now, timeout )
			);
		}
	}

	SChange setDynamicInfoData( final SBase<?> sbase, final SData sdata, final DynamicDataJsonNode data ) {
		sbase.lock();
		try {
			return sbase.unpack( data.data, data.revision, sdata );
		} catch( final Exception e ) {
			sbase.override( data.revision );
			return null;
		} finally {
			sbase.unlock( this );
		}
	}

	SetDynamicDataMap setDynamicInfoData( SetDynamicDataMap result, final String name, final Map<String, DynamicDataJsonNode> nameToData, Map<String, SData> nameToSData, final DynamicDataJsonNode data, final boolean allowCompaction ) {
		final SData sdata = nameToSData.get( name );
		if( sdata != null ) {
			final SBase<?> sbase = sdata.get();
			if( sbase != null && sbase.getType() == data.type && ((sbase.getRevision() < data.revision) || (sbase.isLoose() && sbase.getRevision() == data.revision)) ) {
				if( sbase.isReadOnly() ) {
					sbase.override( data.revision );
				} else {
					result = SetDynamicDataMap.put( result, name, setDynamicInfoData( sbase, sdata, data ) );
				}
			}

			if( allowCompaction && sbase.isSoft() && data.revision == sdata.getAuthorizedRevision() ) {
				sbase.compact( data.revision );
			}
		}

		return result;
	}

	SetDynamicDataMap setDynamicInfoData( final Controller origin, final Map<String, DynamicDataJsonNode> nameToData, final boolean allowCompaction ) {
		SetDynamicDataMap result = null;

		if( nameToData != null ) {
			final Map<String, SData> nameToSData = originToSData.get( origin );
			if( nameToSData != null ) {
				for( final Map.Entry<String, DynamicDataJsonNode> entry: nameToData.entrySet() ){
					final String name = entry.getKey();
					final DynamicDataJsonNode dynamicData = entry.getValue();
					if( SContainers.isNotId( name ) ) {
						result = setDynamicInfoData( result, name, nameToData, nameToSData, dynamicData, allowCompaction );
					}
				}
			}
		}

		return result;
	}

	SetDynamicInfoMap setDynamicInfo( final Map<String, ReceivedDynamicInfo> nameToInfo, final boolean allowCompaction, final Map<String, ? extends Controller> nameToController ){
		SetDynamicInfoMap result = null;

		if( nameToInfo != null ) {
			for( final Map.Entry<String, ReceivedDynamicInfo> entry: nameToInfo.entrySet() ){
				final String name = entry.getKey();
				final ReceivedDynamicInfo dynamicInfo = entry.getValue();
				final Controller controller = nameToController.get( name );
				if( controller != null ) {
					result = SetDynamicInfoMap.put( result, name, controller.setDynamicInfo( this, dynamicInfo, allowCompaction ) );
				}
			}
		}

		return result;
	}

	SetDynamicInfo setDynamicInfo( final Controller origin, final ReceivedDynamicInfo info, final boolean allowCompaction ) {
		if( info == null ) return null;

		lock();
		try{
			return SetDynamicInfo.create(
				setDynamicInfoData( origin, info.nameToData, allowCompaction ),
				setDynamicInfo( info.nameToInfo, allowCompaction, controllerNameToController )
			);
		} finally {
			unlock( origin );
		}
	}

	SetDynamicDataMap setSContainerDynamicInfoData( final Controller origin, final Map<String, DynamicDataJsonNode> nameToData, final boolean allowCompaction, final Reference<Boolean> hasNonSContainer ) {
		SetDynamicDataMap result = null;

		if( nameToData != null ) {
			final Map<String, SData> nameToSData = originToSData.get( origin );
			if( nameToSData != null ) {
				for( final Map.Entry<String, DynamicDataJsonNode> entry: nameToData.entrySet() ){
					final String name = entry.getKey();
					final DynamicDataJsonNode dynamicData = entry.getValue();
					if( SContainers.isId( name ) ) {
						result = setDynamicInfoData( result, name, nameToData, nameToSData, dynamicData, allowCompaction );
					} else {
						hasNonSContainer.set( true );
					}
				}

				if( result != null ) {
					for( final ControllerDynamicInfoHandler handler: dynamicInfoHandlers ) {
						handler.handle( origin, result );
					}
				}
			}
		}

		return result;
	}

	SetDynamicInfoMap setSContainerDynamicInfo( final Map<String, ReceivedDynamicInfo> nameToInfo, final boolean allowCompaction, final Reference<Boolean> hasNonSContainer, final Map<String, ? extends Controller> nameToController ){
		SetDynamicInfoMap result = null;

		if( nameToInfo != null ) {
			for( final Map.Entry<String, ReceivedDynamicInfo> entry: nameToInfo.entrySet() ){
				final String name = entry.getKey();
				final ReceivedDynamicInfo dynamicInfo = entry.getValue();
				final Controller controller = nameToController.get( name );
				if( controller != null ) {
					result = SetDynamicInfoMap.put( result, name, controller.setSContainerDynamicInfo( this, dynamicInfo, allowCompaction, hasNonSContainer ) );
				}
			}
		}

		return result;
	}

	SetDynamicInfo setSContainerDynamicInfo( final Controller origin, final ReceivedDynamicInfo info, final boolean allowCompaction, final Reference<Boolean> hasNonSContainer ) {
		if( info == null ) return null;

		lock();
		try{
			return SetDynamicInfo.create(
				setSContainerDynamicInfoData( origin, info.nameToData, allowCompaction, hasNonSContainer ),
				setSContainerDynamicInfo( info.nameToInfo, allowCompaction, hasNonSContainer, controllerNameToController )
			);
		} finally {
			unlock( origin );
		}
	}

	void triggerOnChangeData( final Controller origin, final Map<String, SChange> nameToSChange ){
		if( nameToSChange == null ) return;

		for( final Map.Entry<String, SChange> entry: nameToSChange.entrySet() ){
			onChange( origin, this, entry.getKey(), entry.getValue() );
		}
	}

	void triggerOnChange( final Map<String, SChangeInfo> nameToInfo, final Map<String, ? extends Controller> nameToController ){
		if( nameToInfo == null || nameToController == null ) return;

		for( final Map.Entry<String, SChangeInfo> entry: nameToInfo.entrySet() ){
			final String name = entry.getKey();
			final SChangeInfo info = entry.getValue();

			final Controller controller = nameToController.get(name);
			if( controller != null ){
				controller.triggerOnChange( this, info );
			}
		}
	}

	void triggerOnChange( final Controller origin, final SChangeInfo schanges ){
		if( schanges == null ) return;

		triggerOnChangeData( origin, schanges.nameToData );
		triggerOnChange( schanges.nameToInfo, controllerNameToController );
	}

	@Override
	public Unlocker lock(){
		lock.lock();
		return unlocker;
	}

	@Override
	public boolean tryLock(){
		return lock.tryLock();
	}

	@Override
	public boolean tryLock( final long timeout, final TimeUnit unit ){
		try {
			return lock.tryLock(timeout, unit);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean isLocked() {
		return lock.isLocked();
	}

	@Override
	public boolean isLockedByCurrentThread() {
		return lock.isHeldByCurrentThread();
	}

	@Override
	public void unlock() {
		// Propagate the change flag
		if( isChanged ){
			isChanged = false;
			update();
		}

		// Unlock
		int count = lock.getHoldCount();
		if( count <= 1 ) {
			// The last unlock
			onUnlock();
		} else {
			// Otherwise
			lock.unlock();
		}
	}

	public void unlock( final Controller origin ) {
		unlock();
	}

	@Override
	public void onUnlock(){
		for( final Controller parent: parents ) {
			parent.onUnlock();
		}
	}

	@Override
	public void update(){
		for( final Controller parent: parents ){
			parent.update();
		}
	}

	@Override
	public String getName(){
		return name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getParent(){
		for( final Controller parent: parents ){
			return (T) parent.getInstance();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Collection<T> getParents(){
		final Set<T> result = new HashSet<T>();
		for( final Controller parent: parents ){
			result.add( (T) parent.getInstance() );
		}
		return result;
	}

	@Override
	public Factory<?> getParentAsFactory(){
		return this.<Factory<?>>getParent();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getActivePage(){
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			final Controller page = controllerNameToController.get( this.pageActive.get() );
			return ( page != null ? (T) page.getInstance() : null );
		}
	}

	public Object getInstance(){
		return instance;
	}

	@Override
	public String getParameter( final String key ){
		final String[] parameters = baggage.parameters.get( key );
		if( parameters == null || parameters.length <= 0 ) return null;
		return parameters[ 0 ];
	}

	@Override
	public String[] getParameters( final String key ){
		return baggage.parameters.get( key );
	}

	@Override
	public Map<String, String[]> getParameterMap(){
		return baggage.parameters;
	}

	@Override
	public ArrayNode getFactoryParameters(){
		return factoryParameters;
	}

	@Override
	public ControllerAttributes getAttributes(){
		return baggage.attributes;
	}

	Map<String, SBase<?>> setSScalarFields(){
		final Map<String, SBase<?>> result = new HashMap<>();
		setSScalarFields( result, factory.sscalars );
		return result;
	}

	Set<SContainer<?, ?>> setSContainerFields() {
		final Set<SContainer<?, ?>> result = new HashSet<>();
		setSContainerFields( result, factory.scontainers );
		return result;
	}

	private void checkFieldValue( final Field field, final Object value ) {
		if( value != null ) return;
		throw new IllegalStateException( "Unexpected 'null' in field '"+field+"'" );
	}

	private void logInitializationError( final Object field, final Exception e ) {
		logger.error("Failed to initialize '{}'", field, e);
	}

	public void put( final String name, final SBase<?> sbase ) {
		sbaseNameToSBase.put( name, sbase );
	}

	public void put( final Object origin, final String name, final SBase<?> sbase ) {
		final Map<String, SData> nameToSData = originToSData.get( origin );
		if( nameToSData != null ) {
			nameToSData.put( name, new SData( sbase ) );
		}
	}

	public void put( final String name, final ControllerOnChangeHandler handler ) {
		onChangeHandlers.put( name, handler );
	}

	public void put( final ControllerDynamicInfoHandler handler ) {
		dynamicInfoHandlers.add( handler );
	}

	void setSContainerFields( final Set<SContainer<?, ?>> result, final Map<String, ControllerFactoryFieldInfo> fieldNameToFieldInfo ){
		for( final ControllerFactoryFieldInfo info: fieldNameToFieldInfo.values() ){
			final Field field = info.field;
			final EnumSet<Property> properties = info.properties;
			try {
				ReflectionUtils.makeAccessible( field );
				final SContainer<?, ?> value = (SContainer<?, ?>) field.get(instance);
				checkFieldValue( field, value );
				value.init( info.name, this, lock, info.generics[ 0 ], properties );
				result.add( value );
			} catch (final Exception e) {
				logInitializationError( field, e );
			}
		}
	}

	void setSScalarFields( final Map<String, SBase<?>> result, final Map<String, ControllerFactoryFieldInfo> fieldInfos ){
		for( final ControllerFactoryFieldInfo info: fieldInfos.values() ){
			final Field field = info.field;
			final EnumSet<Property> properties = info.properties;
			try {
				ReflectionUtils.makeAccessible( field );

				final SBase<?> value = (SBase<?>) field.get(instance);
				checkFieldValue( field, value );

				value.setParent(this);
				value.setLock(lock);
				value.setReadOnly( properties.contains( Property.READ_ONLY ) );
				value.setNonNull( properties.contains( Property.NON_NULL ) );
				value.setSoft( properties.contains( Property.SOFT ) );
				if( properties.contains( Property.UNINITIALIZED ) ) {
					value.uninitialize();
				}

				result.put( info.name, value );
			} catch (final Exception e) {
				logInitializationError( field, e );
			}
		}
	}

	Map<String, Controller> setControllerFields(){
		final Map<String, Controller> result = new HashMap<>();
		setControllerFields( result );
		setFactoryFields( result );
		return result;
	}

	void setControllerFields( final Map<String, Controller> result ){
		for( final ControllerFactoryControllerInfo info: factory.controllers.values() ){
			final Field field = info.field;
			final String name = info.name;
			try {
				ReflectionUtils.makeAccessible( field );
				final Object value = field.get(instance);
				checkFieldValue( field, value );

				Controller controller = null;
				switch( info.type ) {
				case PAGE:
					controller = info.factory.createPage(name, this, value, baggage, lock, tasks);
					break;
				case POPUP:
					controller = info.factory.createPopup(name, this, value, baggage, lock, tasks, info.properties.contains(Property.PRIMARY));
					break;
				case COMPONENT:
					controller = info.factory.createComponent(name, this, value, baggage, lock, tasks);
					break;
				case SHARED_COMPONENT:
					controller = info.factory.createSharedComponent(name, this, value, baggage);
					break;
				default:
					break;
				}
				if( controller != null ) {
					result.put(name, controller);
				}
			} catch (final Exception e) {
				logInitializationError( field, e );
			}
		}
	}

	void setFactoryFields( final Map<String, Controller> result ){
		for( final ControllerFactoryControllerInfo info: factory.factories.values() ){
			final Field field = info.field;
			final String name = info.name;
			try {
				ReflectionUtils.makeAccessible( field );
				final Object value = field.get(instance);
				checkFieldValue( field, value );
				final FactoryController<?> controller = info.factory.createFactory( info.type, name, this, value, baggage, lock, tasks, info.childFactory );
				result.put(name, controller);
			} catch (final Exception e) {
				logInitializationError( field, e );
			}
		}
	}

	void onCreate( final Object[] parameters ){
		final VoidParametrizedMethods methods = factory.onCreateMethods;
		final TrackingData trackingData = methods.getTrackingData( this, instance );
		methods.call(this, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, parameters);

		if( methods.containsLockNotRequired() ) {
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.call(Controller.this, trackingData, null, LockRequirements.NOT_REQUIRED, instance, parameters);
				}
			});
		}
	}

	public void create( final Object... parameters ) {
		for( final Controller controller: controllerNameToController.values() ){
			controller.create( parameters );
		}

		onCreate( parameters );
	}

	void onPostCreate( final Object[] parameters ){
		final VoidParametrizedMethods methods = factory.onPostCreateMethods;
		final TrackingData trackingData = methods.getTrackingData( this, instance );
		methods.call(this, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, parameters);

		if( methods.containsLockNotRequired() ) {
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.call(Controller.this, trackingData, null, LockRequirements.NOT_REQUIRED, instance, parameters);
				}
			});
		}
	}

	public void postCreate( final Object... parameters ) {
		for( final Controller controller: controllerNameToController.values() ){
			controller.postCreate( parameters );
		}

		onPostCreate( parameters );
	}

	public void checkVisibility(){
		final boolean newVisibility = isShown();
		synchronized( visibility ){
			final boolean oldVisibility = visibility.get();
			if( oldVisibility != newVisibility ){
				visibility.set(newVisibility);
				if( newVisibility ){
					onShow();
				} else {
					onHide();
				}
			}
		}

		// Controller
		for( final Controller controller: controllerNameToController.values() ){
			controller.checkVisibility();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onChange( final SParent origin, final SParent parent, final String name, final SChange schange ){
		final Object[] parameters = schange.toParameters();
		switch( name ) {
		case PAGE_ACTIVE_ID: {
				onChange( "page", parameters );

				final String oldPage = (String) parameters[ 0 ];
				final String newPage = (String) parameters[ 1 ];
				if( oldPage != null ) {
					final Controller page = controllerNameToController.get( (String)oldPage );
					if( page != null ) page.checkVisibility();
				}
				if( newPage != null ) {
					final Controller page = controllerNameToController.get( (String)newPage );
					if( page != null ) page.checkVisibility();
				}
			}
			break;
		case CALL_REQUESTS_ID: {
				final Map<String, CallRequest> added = (Map<String, CallRequest>) parameters[ 0 ];
				final Map<String, CallRequest> removed = (Map<String, CallRequest>) parameters[ 1 ];
				final CallSMaps callSMaps = originToCallSMaps.get( origin );
				if( callSMaps != null ) {
					for( final String key: removed.keySet() ){
						callSMaps.removeResult( key );
					}

					for( final Map.Entry<String, CallRequest> entry: added.entrySet() ){
						if( callSMaps.containsResultKey( entry.getKey() ) != true ) {
							call( callSMaps, entry.getKey(), entry.getValue() );
						}
					}
				}
			}
			break;
		case CALL_RESULTS_ID: {
				final Map<String, Object> added = (Map<String, Object>) parameters[ 0 ];
				final Map<String, Update<Object>> updated = (Map<String, Update<Object>>) parameters[ 1 ];
				final CallSMaps callSMaps = originToCallSMaps.get( origin );
				if( callSMaps != null ) {
					for( final String key: added.keySet() ){
						callSMaps.removeResultIfNotExits( key );
					}

					for( final String key: updated.keySet() ){
						callSMaps.removeResultIfNotExits( key );
					}
				}
			}
			break;
		case TRIGGER_REQUESTS_ID: {
				final Map<String, TriggerRequest> added = (Map<String, TriggerRequest>) parameters[ 0 ];
				final Map<String, Update<TriggerRequest>> updated = (Map<String, Update<TriggerRequest>>) parameters[ 2 ];

				for( final String key: added.keySet() ){
					triggerRequests.remove( key );
				}

				for( final String key: updated.keySet() ){
					triggerRequests.remove( key );
				}
			}
			break;
		case TRIGGER_RESULTS_ID: {
				final Map<String, List<JsonNode>> added = (Map<String, List<JsonNode>>) parameters[ 0 ];
				final Map<String, Update<List<JsonNode>>> updated = (Map<String, Update<List<JsonNode>>>) parameters[ 2 ];

				for( final Map.Entry<String, List<JsonNode>> entry: added.entrySet() ){
					resolveTriggerRequest( origin, entry.getKey(), entry.getValue() );
				}

				for( final Map.Entry<String, Update<List<JsonNode>>> entry: updated.entrySet() ){
					resolveTriggerRequest( origin, entry.getKey(), entry.getValue().getNewValue() );
				}
			}
			break;
		default: {
				final ControllerOnChangeHandler handler = onChangeHandlers.get( name );
				if( handler != null ) {
					handler.handle( origin, parent, name, schange );
				} else {
					if( name != null ) {
						onChange( name, parameters );
					}
				}
			}
			break;
		}
	}

	private void resolveTriggerRequest( final SParent origin, final String id, final List<JsonNode> result ) {
		if( originToTriggerResults.size() <= 1 ) {
			final TriggerRequest request = triggerRequests.remove( id );
			if( request != null && request.deferred != null ) {
				request.deferred.resolve( result );
			}
		} else {
			final TriggerRequest request = removeTriggerRequest( origin, id );
			if( request != null && request.deferred != null ) {
				request.deferred.resolve( makeTriggerResult( id ) );
			}
		}
	}

	private TriggerRequest removeTriggerRequest( final SParent origin, final String id ) {
		for( final Map.Entry<SParent, SMapImpl<List<JsonNode>>> entry: originToTriggerResults.entrySet() ) {
			if( entry.getKey() != origin && entry.getValue().containsKey( id ) != true ) return null;
		}
		return triggerRequests.remove( id );
	}

	private List<JsonNode> makeTriggerResult( final String id ) {
		final List<JsonNode> result = new ArrayList<>();
		for( final SMapImpl<List<JsonNode>> triggerResults: originToTriggerResults.values() ) {
			final List<JsonNode> triggerResult = triggerResults.get( id );
			if( triggerResult != null ) {
				result.addAll( triggerResult );
			}
		}
		return result;
	}

	public void onChange( final String name, final Object[] parameters ) {
		final VoidTypedParametrizedMethods methods = factory.onChangeMethods;
		final TrackingData trackingData = methods.getTrackingDataNonDefaults( name, this, instance );
		methods.callNonDefaults(this, name, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, parameters);

		if( methods.containsLockNotRequired( name ) ) {
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.callNonDefaults(Controller.this, name, trackingData, null, LockRequirements.NOT_REQUIRED, instance, parameters);
				}
			});
		}

		final String path = this.name + "." + name;
		for( final Controller parent: parents ) {
			parent.onChange( path, parameters );
		}
	}

	public void onNotice( final Object origin, final String name, final Object... parameters ) {
		final VoidTypedParametrizedMethods methods = factory.onNoticeMethods;
		final TrackingData trackingData = methods.getTrackingData( name, this, instance );
		methods.call(this, name, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, parameters);

		if( methods.containsLockNotRequired() || methods.containsLockNotRequired( name ) ) {
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.call(Controller.this, name, trackingData, null, LockRequirements.NOT_REQUIRED, instance, parameters);
				}
			});
		}

		final String path = this.name + "." + name;
		for( final Controller parent: parents ) {
			parent.onNotice(origin, path, parameters);
		}
	}

	@Override
	public void show(){
		// DO NOTHING
	}

	@Override
	public void hide(){
		// DO NOTHING
	}

	@Override
	public boolean isShown(){
		for( final Controller parent: parents ) {
			return parent.isShown();
		}
		return true;
	}

	@Override
	public boolean isHidden(){
		return !isShown();
	}

	public void onShow( final String name ){
		final VoidTypedParametrizedMethods methods = factory.onShowMethods;
		final TrackingData trackingData = methods.getTrackingDataNonDefaults(name, this, instance);
		methods.call(this, name, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, name);

		if( methods.containsLockNotRequired() || methods.containsLockNotRequired( name ) ) {
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.call(Controller.this, name, trackingData, null, LockRequirements.NOT_REQUIRED, instance, name);
				}
			});
		}

		for( final Controller parent: parents ) {
			parent.onShow(this.name + "." + name);
		}
	}

	public void onShow(){
		final VoidTypedParametrizedMethods methods = factory.onShowMethods;
		final TrackingData trackingData = methods.getTrackingDataDefaults(this, instance);
		methods.call(this, AbstractTypedMethods.TYPE_SELF, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, name);

		if( methods.containsLockNotRequired() || methods.containsLockNotRequired( AbstractTypedMethods.TYPE_SELF ) ) {
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.call(Controller.this, AbstractTypedMethods.TYPE_SELF, trackingData, null, LockRequirements.NOT_REQUIRED, instance, name);
				}
			});
		}

		for( final Controller parent: parents ) {
			parent.onShow(name);
		}
	}

	public void onHide( final String name ){
		final VoidTypedParametrizedMethods methods = factory.onHideMethods;
		final TrackingData trackingData = methods.getTrackingDataNonDefaults(name, this, instance);
		methods.call(this, name, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, name);

		if( methods.containsLockNotRequired() || methods.containsLockNotRequired( name ) ) {
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.call(Controller.this, name, trackingData, null, LockRequirements.NOT_REQUIRED, instance, name);
				}
			});
		}

		for( final Controller parent: parents ) {
			parent.onHide(this.name + "." + name);
		}
	}

	public void onHide(){
		final VoidTypedParametrizedMethods methods = factory.onHideMethods;
		final TrackingData trackingData = methods.getTrackingDataDefaults(this, instance);
		methods.call(this, AbstractTypedMethods.TYPE_SELF, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, name);

		if( methods.containsLockNotRequired() || methods.containsLockNotRequired( AbstractTypedMethods.TYPE_SELF ) ) {
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.call(Controller.this, AbstractTypedMethods.TYPE_SELF, trackingData, null, LockRequirements.NOT_REQUIRED, instance, name);
				}
			});
		}

		for( final Controller parent: parents ) {
			parent.onHide(name);
		}
	}

	public void onDestroy() {
		// Controller
		for( final Controller controller: controllerNameToController.values() ){
			controller.onDestroy();
		}

		// Call destroy handler
		final VoidParametrizedMethods methods = factory.onDestroyMethods;
		final TrackingData trackingData = methods.getTrackingData(this, instance);
		methods.call(this, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance);

		if( methods.containsLockNotRequired() ) {
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.call(Controller.this, trackingData, null, LockRequirements.NOT_REQUIRED, instance);
				}
			});
		}

		// Cancel all interval/timeout
		cancelAll();
	}

	public void destroy( final Controller origin ){
		try( final Unlocker unlocker = lock() ) {
			// Base
			originToSData.remove( origin );

			// Callable
			final CallSMaps callSMaps = originToCallSMaps.remove( origin );
			if( callSMaps != null ) {
				callSMaps.removeOrigin( origin );
			}

			// Trigger
			final SMapImpl<List<JsonNode>> triggerResults = originToTriggerResults.remove( origin );
			if( triggerResults != null ) {
				triggerResults.removeOrigin( origin );
			}

			// Trigger directs
			originToTriggerDirects.remove( origin );

			// Container
			for( final SContainer<?, ?> scontainer: scontainers ) {
				scontainer.removeOrigin( origin );
			}

			// Method data
			workingDatas.clear();

			// Controller
			for( final Controller controller: controllerNameToController.values() ){
				controller.destroy( this );
			}
		}
	}

	void call( final CallSMapsPut callSMapsPut, final String callKey, final CallRequest callRequest ){
		try( final Unlocker unlocker = lock() ) {
			final String name = callRequest.name;
			final Object[] parameters = AbstractMethods.toParameters( callRequest.parameters, 0 );

			final CallableMethods<Object> methods = factory.callables;
			final TrackingData trackingData = methods.getTrackingData( name, this, instance );
			final MethodResult<Object> result = methods.call(this, name, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, parameters);
			if( result != null ) {
				completeCall( callSMapsPut, callKey, result, name );
			} else if( methods.containsLockNotRequired( name ) ) {
				baggage.scheduler.execute(new Runnable(){
					@Override
					public void run() {
						final MethodResult<Object> result = methods.call(Controller.this, name, trackingData, null, LockRequirements.NOT_REQUIRED, instance, parameters);
						try( final Unlocker unlocker = lock() ) {
							completeCall( callSMapsPut, callKey, result, name );
						}
					}
				});
			} else {
				callSMapsPut.putErrorIfExists( callKey, CallResultType.NO_SUCH_METHOD );
			}
		}
	}

	void completeCall( final CallSMapsPut callSMapsPut, final String callKey, final MethodResult<Object> callResult, final String name ){
		if( callResult instanceof MethodResultData ){
			final MethodResultData<?> callResultData = (MethodResultData<?>) callResult;
			callSMapsPut.putResultIfExists( callKey, callResultData.data );
		} else if( callResult instanceof MethodResultException ) {
			final MethodResultException<?> callResultException = (MethodResultException<?>) callResult;
			final Throwable throwable = callResultException.getInvocationTargetThrowable();
			if( throwable != null ) {
				final Class<? extends Throwable> throwableClass = throwable.getClass();
				if( handleCallException( callSMapsPut, callKey, this, name, throwableClass, throwable, callResultException.getMethod() ) != true ) {
					callSMapsPut.putErrorIfExists( callKey, CallResultType.EXCEPTION );
					this.handle( callResult );
				}
			} else {
				callSMapsPut.putErrorIfExists( callKey, CallResultType.EXCEPTION );
				this.handle( callResult );
			}
		} else if( callResult instanceof MethodResultVoid ) {
			callSMapsPut.putVoidIfExists( callKey );
		} else {
			callSMapsPut.putErrorIfExists( callKey, CallResultType.NO_SUCH_METHOD );
		}
	}

	boolean handleCallException( final CallSMapsPut callSMapsPut, final String callKey, final Controller controller, final String name, final Class<? extends Throwable> throwableClass, final Throwable throwable, final Method method ) {
		final TypedExceptionHandlerMethods<String> handlers = controller.factory.callableExceptionHandlers;
		final ExceptionHandlerMethod<String> handler = handlers.find( name, throwableClass );
		if( handler != null ) {
			final TrackingData handlerTrackingData = handlers.getTrackingData( name, controller, controller.instance );
			if( LockRequirements.REQUIRED_OR_UNSPECIFIED.contains( handler.getLockRequirement() ) ) {
				final MethodResult<String> handlerResult = handler.call( controller, handlerTrackingData, null, controller.instance, throwable, method );
				if( handlerResult instanceof MethodResultData ) {
					callSMapsPut.putErrorIfExists( callKey, ((MethodResultData<String>)handlerResult).data );
				} else {
					callSMapsPut.putErrorIfExists( callKey, CallResultType.EXCEPTION );
				}
				controller.handle( handlerResult );
			} else {
				controller.baggage.scheduler.execute(new Runnable(){
					@Override
					public void run() {
						final MethodResult<String> handlerResult = handler.call( controller, handlerTrackingData, null, controller.instance, throwable, method );
						try( final Unlocker unlocker = controller.lock() ) {
							if( handlerResult instanceof MethodResultData ) {
								callSMapsPut.putErrorIfExists( callKey, ((MethodResultData<String>)handlerResult).data );
							} else {
								callSMapsPut.putErrorIfExists( callKey, CallResultType.EXCEPTION );
							}
							controller.handle( handlerResult );
						}
					}
				});
			}
			return true;
		}

		if( (controller instanceof SharedComponentController) != true ) {
			final String newName = controller.getName()+"."+name;
			for( final Controller parent: controller.parents ) {
				if( handleCallException( callSMapsPut, callKey, parent, newName, throwableClass, throwable, method ) ) {
					return true;
				}
			}
		}

		return false;
	}

	public StaticInfo getStaticInfo() {
		return factory.staticInfo;
	}

	public boolean hasHistorical(){
		return factory.hasHistorical;
	}

	public StaticInstanceInfo getStaticInstanceInfo(){
		try( final Unlocker unlocker = lock() ) {
			final StaticInstanceInfo result = new StaticInstanceInfo();
			boolean isEmpty = true;

			// Constants
			for( final Field field: factory.constantFields ){
				try {
					ReflectionUtils.makeAccessible( field );
					final String name = field.getName();
					final Object value = field.get( instance );
					result.constants.put( name , value );
					isEmpty = false;
				} catch ( final Exception e ) {
					logger.error( e.getMessage(), e );
				}
			}

			// Controllers
			for( final Map.Entry<String, Controller> entry: controllerNameToController.entrySet() ){
				final String name = entry.getKey();
				final StaticInstanceInfo info = entry.getValue().getStaticInstanceInfo();
				if( info == null ) continue;
				result.put(name, info);
				isEmpty = false;
			}

			return (isEmpty ? null : result);
		}
	}

	@Override
	public String getRemoteAddress() {
		return baggage.remoteAddress;
	}

	@Override
	public Principal getPrincipal() {
		return baggage.principal;
	}

	@Override
	public Scheduler getScheduler(){
		return baggage.scheduler;
	}

	@Override
	public String getSessionId(){
		return baggage.sessionId;
	}

	@Override
	public String getSubSessionId(){
		return baggage.subSessionId;
	}

	@Override
	public void execute( final String name, final Object... parameters ){
		final VoidTypedParametrizedMethods methods = factory.onTimeMethods;
		final TrackingData trackingData = methods.getTrackingData(name, this, instance);
		baggage.scheduler.execute(new Runnable(){
			@Override
			public void run() {
				methods.call(Controller.this, name, trackingData, null, LockRequirements.NOT_REQUIRED_OR_UNSPECIFIED, instance, parameters);

				if( methods.containsLockRequired() || methods.containsLockRequired( name ) ) {
					try( final Unlocker unlocker = lock() ) {
						methods.call(Controller.this, name, trackingData, null, LockRequirements.REQUIRED, instance, parameters);
					}
				}
			}
		});
	}

	@Override
	public Future<?> execute( final Runnable runnable ){
		return baggage.scheduler.submit( runnable );
	}

	@Override
	public <T> Future<T> execute( final Callable<T> callable ){
		return baggage.scheduler.submit( callable );
	}

	@Override
	public long timeout( final String name, final long delay, final Object... parameters ) {
		final long id = timeId.getAndIncrement();
		final VoidTypedParametrizedMethods methods = factory.onTimeMethods;
		final TrackingData trackingData = methods.getTrackingData(name, this, instance);
		times.put( id, new ConcurrentDataTimeout( name, this, methods, trackingData, parameters ) );
		baggage.scheduler.schedule( new ConcurrentRunnable( id, times, true ), delay );
		return id;
	}

	@Override
	public long timeout( final Runnable runnable, final long delay ) {
		final long id = timeId.getAndIncrement();
		times.put( id, new ConcurrentDataTimeoutRunnable( this, runnable ) );
		baggage.scheduler.schedule( new ConcurrentRunnable( id, times, true ), delay );
		return id;
	}

	@Override
	public <T> TimeoutFuture<T> timeout( final Callable<T> callable, final long delay ) {
		final AtomicReference<T> result = new AtomicReference<T>( null );
		final long id = timeId.getAndIncrement();
		times.put( id, new ConcurrentDataTimeoutCallable<T>( this, callable, result ) );
		final ScheduledFuture<?> future = baggage.scheduler.schedule( new ConcurrentRunnable( id, times, true ), delay );
		return new TimeoutFutureImpl<T>( this, future, result, id );
	}

	@Override
	public long interval( final String name, final long interval ){
		return interval( name, interval, interval );
	}

	@Override
	public long interval( final String name, final long startAfter, final long interval, final Object... parameters ) {
		final long id = timeId.getAndIncrement();
		final VoidTypedParametrizedMethods methods = factory.onTimeMethods;
		final TrackingData trackingData = methods.getTrackingData(name, this, instance);
		times.put( id, new ConcurrentDataInterval( this, name, methods, trackingData, interval, parameters ) );
		baggage.scheduler.schedule( new ConcurrentRunnable( id, times, false ), startAfter );
		return id;
	}

	@Override
	public long interval( final Runnable runnable, final long interval ){
		return interval( runnable, interval, interval );
	}

	@Override
	public long interval( final Runnable runnable, final long startAt, final long interval ) {
		final long id = timeId.getAndIncrement();
		times.put(id, new ConcurrentDataIntervalRunnable( this, runnable, interval ));
		baggage.scheduler.schedule( new ConcurrentRunnable( id, times, false ), startAt );
		return id;
	}

	@Override
	public boolean cancel( final long id ){
		return ( times.remove(id) != null );
	}

	@Override
	public Long getRequestId(){
		return timeIdLocal.get();
	}

	@Override
	public void setRequestId( final Long id ){
		timeIdLocal.set( id );
	}

	@Override
	public TrackingIds getTrackingIds(){
		return trackingIdsLocal.get();
	}

	@Override
	public void setTrackingIds( final TrackingIds ids ){
		trackingIdsLocal.set( ids );
	}

	@Override
	public boolean cancel(){
		final TaskInternal task = this.task.get();
		if( task != null ) {
			try( final Unlocker unlocker = lock() ) {
				return task.cancel( TaskResultType.CANCELED );
			}
		} else {
			final Long id = getRequestId();
			if( id != null ) {
				return cancel( id );
			}
			return false;
		}
	}

	@Override
	public boolean cancel( final String reason ){
		final TaskInternal task = this.task.get();
		if( task != null ) {
			try( final Unlocker unlocker = lock() ) {
				return task.cancel( reason );
			}
		}
		return false;
	}

	@Override
	public void cancelAll(){
		times.clear();
	}

	@Override
	public boolean isCanceled(){
		final TaskInternal task = this.task.get();
		if( task != null ) {
			try( final Unlocker unlocker = lock() ) {
				return task.isCanceled();
			}
		} else {
			final Long id = getRequestId();
			if( id != null ) {
				return !times.containsKey( id );
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean isHeadCall(){
		final TrackingIds trackingIds = getTrackingIds();
		if( trackingIds == null ) throw new UnsupportedOperationException();
		return trackingIds.isHead();
	}

	private String getNextTriggerId() {
		return triggerIdPrefix + Long.toString(_triggerId.getAndIncrement(), 32);
	}

	@Override
	public void trigger( final String name, final Object... arguments ){
		try( final Unlocker unlocker = this.lock() ) {
			triggerRequests.put( getNextTriggerId(), TriggerRequests.create( name, arguments, null ) );
			if( 0 < MAXIMUM_TRIGGER_QUEUE_SIZE && MAXIMUM_TRIGGER_QUEUE_SIZE < triggerRequests.size() ) {
				triggerRequests.pollFirstEntry();
			}
		}
	}

	@Override
	public void triggerDirect( final String name, final Object... arguments ){
		try( final Unlocker unlocker = this.lock() ) {
			for( final SClassImpl<List<TriggerRequest>> triggerDirects: originToTriggerDirects.values() ) {
				List<TriggerRequest> triggerRequests = triggerDirects.get();
				if( triggerRequests == null ) {
					triggerRequests = new ArrayList<>();
				}
				triggerRequests.add( TriggerRequests.create( name, arguments, null ) );
				triggerDirects.set( triggerRequests );
				if( 0 < MAXIMUM_TRIGGER_QUEUE_SIZE && MAXIMUM_TRIGGER_QUEUE_SIZE < triggerRequests.size() ) {
					triggerRequests.remove( 0 );
				}
			}
		}
	}

	@Override
	public TriggerResult triggerAndWait( final String name, final long timeout, final Object... arguments ){
		final Deferred<List<JsonNode>, TriggerErrors, Integer> deferred
			= new DeferredObject<List<JsonNode>, TriggerErrors, Integer>();

		final String id = getNextTriggerId();
		triggerRequests.put( id, TriggerRequests.create( name, arguments, deferred ) );
		if( 0 < MAXIMUM_TRIGGER_QUEUE_SIZE && MAXIMUM_TRIGGER_QUEUE_SIZE < triggerRequests.size() ) {
			final TriggerRequest removed = triggerRequests.pollFirstEntry().getValue();
			if( removed != null && removed.deferred != null ) {
				removed.deferred.reject(TriggerErrors.EXCEEDED);
			}
		}

		baggage.scheduler.schedule( new Runnable(){
			@Override
			public void run() {
				final TriggerRequest request = triggerRequests.remove( id );
				if( request != null && request.deferred != null ) {
					request.deferred.reject(TriggerErrors.TIMEOUT);
				}
			}
		}, timeout );

		return new TriggerResult(deferred.promise());
	}

	public void show(final String name) {
		pageActive.set(name);
	}

	public void hide(final String name) {
		pageActive.compareAndSet(name, null);
	}

	public boolean isShown(final String name) {
		return name != null && name.equals(pageActive.get());
	}

	@Override
	public boolean isReadOnly() {
		return factory.properties.contains(Property.READ_ONLY);
	}

	@Override
	public boolean isNonNull() {
		return factory.properties.contains(Property.NON_NULL);
	}

	@Override
	public boolean isHistorical() {
		return factory.properties.contains(Property.HISTORICAL);
	}

	public boolean isShared() {
		return false;
	}

	@Override
	public List<Locale> getLocales() {
		return baggage.locales;
	}

	@Override
	public Locale getLocale() {
		return baggage.locales.get( 0 );
	}

	@Override
	public void notify(final String name, final Object... parameters) {
		onNotice( this, name, parameters );
	}

	public void notifyAsync(final String name, final Object... parameters) {
		baggage.scheduler.execute(new Runnable(){
			@Override
			public void run() {
				onNotice( this, name, parameters );
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V getWorkingData( final Object key ){
		return (V) workingDatas.get( key );
	}

	@Override
	public void putWorkingData( final Object key, final Object data ){
		workingDatas.put( key, data );
	}

	public Controller getControllerAt( final String[] paths ) {
		return getControllerAt( paths, 0, paths.length );
	}

	public Controller getControllerAt( final String[] paths, final int index, final int length ) {
		if( index < length ) {
			try( final Unlocker unlocker = lock() ) {
				final Controller controller = controllerNameToController.get( paths[ index ] );
				if( controller != null ) {
					return controller.getControllerAt( paths, index+1, length );
				} else {
					return null;
				}
			}
		} else {
			return this;
		}
	}

	public boolean hasOnIdleCheckMethods() {
		final OnIdleCheckMethodInfo info = factory.onIdleCheckMethodInfo;
		return ( info.hasLockNotRequired || info.hasLockRequired );
	}

	public Long checkIdle( final ControllerIo io ) {
		Long result = null;
		final OnIdleCheckMethodInfo info = factory.onIdleCheckMethodInfo;
		if( info.hasLockNotRequired ) {
			result = mergeDelay( result, checkIdleNotRequired( io ) );
		}
		if( info.hasLockRequired ) {
			try( final Unlocker unlocker = this.lock() ) {
				result = mergeDelay( result, checkIdleRequired( io ) );
			}
		}
		return result;
	}

	private Long mergeDelay( final Long delayA, final Long delayB ) {
		if( delayA != null ) {
			if( delayB != null ) {
				return Math.min(delayA, delayB);
			} else {
				return delayA;
			}
		}
		return delayB;
	}

	protected Long checkIdleNotRequired( final ControllerIo io ) {
		Long result = null;
		for( final Controller controller: controllerNameToController.values() ){
			result = mergeDelay( result, controller.checkIdleNotRequired( io ) );
		}
		return mergeDelay( result, onIdleCheck( io, LockRequirements.NOT_REQUIRED ) );
	}

	protected Long checkIdleRequired( final ControllerIo io ) {
		Long result = null;
		for( final Controller controller: controllerNameToController.values() ){
			result = mergeDelay( result, controller.checkIdleRequired( io ) );
		}
		return mergeDelay( result, onIdleCheck( io, LockRequirements.REQUIRED_OR_UNSPECIFIED ) );
	}

	private Long onIdleCheck( final ControllerIo io, final EnumSet<LockRequirement> lockRequirements ){
		Long result = null;
		final MethodAndOrders<Long> methods = factory.onIdleCheckMethods;
		if( methods.contains( lockRequirements ) ) {
			final TrackingData trackingData = methods.getTrackingData(this, instance);
			final Collection<MethodResult<Long>> methodResults = methods.call(this, trackingData, null, lockRequirements, instance, io);
			for( final MethodResult<Long> methodResult: methodResults ){
				if( methodResult instanceof MethodResultData ) {
					final MethodResultData<Long> data = (MethodResultData<Long>) methodResult;
					result = mergeDelay( result, data.data );
				}
			}
		}
		return result;
	}
}
