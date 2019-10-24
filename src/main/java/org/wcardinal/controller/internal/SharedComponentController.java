/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.controller.ControllerAttributes;
import org.wcardinal.controller.ControllerIo;
import org.wcardinal.controller.internal.info.SetDynamicInfo;
import org.wcardinal.controller.internal.info.AckInfo;
import org.wcardinal.controller.internal.info.ReceivedDynamicInfo;
import org.wcardinal.controller.internal.info.RejectedSetDynamicInfo;
import org.wcardinal.controller.internal.info.SChangeInfo;
import org.wcardinal.util.Reference;
import org.wcardinal.util.reflection.LockRequirements;
import org.wcardinal.util.reflection.TrackingData;
import org.wcardinal.util.reflection.VoidTypedParametrizedMethods;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

public class SharedComponentController extends ComponentController implements Runnable {
	final static Map<Object, SharedComponentController> components = new HashMap<Object, SharedComponentController>();

	final long SYNC_CLIENT_UPDATE_INTERVAL;

	final AtomicBoolean onCreated = new AtomicBoolean( false );
	final AtomicBoolean onPostCreated = new AtomicBoolean( false );
	final AtomicBoolean onDestroyed = new AtomicBoolean( false );
	final RejectedSetDynamicInfos rejectedInfos = new RejectedSetDynamicInfos();

	protected SharedComponentController(
			final String name, final Controller parent,
			final ControllerFactory factory, final Object instance,
			final ControllerBaggage baggage, final ArrayNode factoryParameters ) {
		super(name, parent, factory, instance, baggage, factoryParameters, new AutoCloseableReentrantLock(), new TaskInternalQueue());

		SYNC_CLIENT_UPDATE_INTERVAL = baggage.configuration.getSyncClientUpdateInterval();
		baggage.scheduler.execute( this );
	}

	public static synchronized SharedComponentController create(
			final String name, final Controller parent,
			final ControllerFactory factory, final Object instance,
			final ControllerBaggage baggage, final ArrayNode factoryParameters) {
		final SharedComponentController controller = components.get(instance);
		if( controller != null ){
			controller.addParent(parent);
			return controller;
		}

		final SharedComponentController newController
			= new SharedComponentController(name, parent, factory, instance, baggage, factoryParameters);

		components.put(instance, newController);

		return newController;
	}


	public static void destroy( final Object instance ){
		final SharedComponentController controller;
		synchronized( SharedComponentController.class ) {
			controller = components.remove(instance);
		}
		if( controller != null ) controller.destroy();
	}

	@Override
	public void create( final Object... parameters ){
		if( onCreated.compareAndSet( false, true ) ) {
			super.create( parameters );
		}
	}

	@Override
	public void postCreate( final Object... parameters ){
		if( onPostCreated.compareAndSet( false, true ) ) {
			super.postCreate( parameters );
		}
	}

	@Override
	public void onDestroy(){

	}

	public void destroy() {
		if( onDestroyed.getAndSet(true) != true ) {
			try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
				super.onDestroy();
				for( final Controller parent: parents ) {
					super.destroy( parent );
				}
			}
		}
	}

	@Override
	public void destroy( final Controller origin ){
		removeParent( origin );
	}

	@Override
	public String getName(){
		throw new UnsupportedOperationException( "getName() is not supported by shared components" );
	}

	@Override
	public ControllerAttributes getAttributes(){
		throw new UnsupportedOperationException( "getAttributes() is not supported by shared components" );
	}

	@Override
	public List<Locale> getLocales(){
		throw new UnsupportedOperationException( "getLocales() is not supported by shared components" );
	}

	@Override
	public Locale getLocale(){
		throw new UnsupportedOperationException( "getLocale() is not supported by shared components" );
	}

	@Override
	public String getRemoteAddress(){
		throw new UnsupportedOperationException( "getRemoteAddress() is not supported by shared components" );
	}

	@Override
	public Principal getPrincipal(){
		throw new UnsupportedOperationException( "getPrincipal() is not supported by shared components" );
	}

	@Override
	public String getSessionId(){
		throw new UnsupportedOperationException( "getSessionId() is not supported by shared components" );
	}

	@Override
	public String getSubSessionId(){
		throw new UnsupportedOperationException( "getSubSessionId() is not supported by shared components" );
	}

	@Override
	public boolean isShared() {
		return true;
	}

	@Override
	public void onChange( final String name, final Object[] parameters ) {
		final VoidTypedParametrizedMethods methods = factory.onChangeMethods;
		final TrackingData trackingData = methods.getTrackingDataNonDefaults(name, this, instance);
		methods.callNonDefaults(this, name, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, parameters );

		if( methods.containsLockNotRequired( name ) ){
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.callNonDefaults(SharedComponentController.this, name, trackingData, null, LockRequirements.NOT_REQUIRED, instance, parameters );
				}
			});
		}

		final String path = this.name + "." + name;
		for( final Controller parent: parents ) {
			onChangeAsync( parent, path, parameters );
		}
	}

	private static void onChangeAsync( final Controller controller, final String name, final Object[] parameters ) {
		controller.baggage.scheduler.execute(new Runnable(){
			@Override
			public void run() {
				try( final Unlocker unlocker = controller.lock() ) {
					controller.onChange( name, parameters );
				}
			}
		});
	}

	@Override
	public void onNotice( final Object origin, final String name, final Object... parameters ) {
		final VoidTypedParametrizedMethods methods = factory.onNoticeMethods;
		final TrackingData trackingData = methods.getTrackingData(name, this, instance);
		methods.call(this, name, trackingData, null, LockRequirements.REQUIRED_OR_UNSPECIFIED, instance, parameters);

		if( methods.containsLockNotRequired() || methods.containsLockNotRequired( name ) ){
			baggage.scheduler.execute(new Runnable(){
				@Override
				public void run() {
					methods.call(SharedComponentController.this, name, trackingData, null, LockRequirements.NOT_REQUIRED, instance, parameters);
				}
			});
		}

		final String path = this.name + "." + name;
		for( final Controller parent: parents ) {
			onNoticeAsync(parent, origin, path, parameters);
		}
	}

	private static void onNoticeAsync( final Controller controller, final Object origin, final String name, final Object... parameters ) {
		controller.baggage.scheduler.execute(new Runnable(){
			@Override
			public void run() {
				try( final Unlocker unlocker = controller.lock() ) {
					controller.onNotice(origin, name, parameters);
				}
			}
		});
	}

	@Override
	public void update(){
		for( final Controller parent: parents ) {
			updateAsync( parent );
		}
	}

	private static void updateAsync( final Controller controller ){
		controller.baggage.scheduler.execute(new Runnable(){
			@Override
			public void run() {
				try( final Unlocker unlocker = controller.lock() ) {
					controller.update();
				}
			}
		});
	}

	@Override
	public void unlock() {
		// Propagate the change flag
		if( isChanged ){
			isChanged = false;
			update();
		}

		// Unlock
		lock.unlock();
	}

	public void unlock( final Controller origin ) {
		// Propagate the change flag
		if( isChanged ){
			isChanged = false;
			update();
		} else {
			for( final Controller parent: parents ) {
				if( parent != origin ) updateAsync( parent );
			}
		}

		// Unlock
		lock.unlock();
	}

	@Override
	public void onUnlock() {
		lock.unlock();
	}

	@Override
	public SetDynamicInfo setDynamicInfo( final Controller origin, final ReceivedDynamicInfo receivedInfo, final boolean allowCompaction ){
		return null;
	}

	@Override
	public SetDynamicInfo setSContainerDynamicInfo( final Controller origin, final ReceivedDynamicInfo receivedInfo, final boolean allowCompaction, final Reference<Boolean> hasNonSContainer ){
		return setSContainerDynamicInfo( origin, receivedInfo );
	}

	SetDynamicInfo setSContainerDynamicInfo( final Controller origin, final ReceivedDynamicInfo receivedInfo ){
		try( final Unlocker unlocker = lock() ) {
			boolean isRejected = false;
			final Reference<Boolean> hasNonSContainer = new Reference<Boolean>( false );
			final SetDynamicInfo scontainerInfo = super.setSContainerDynamicInfo( origin, receivedInfo, false, hasNonSContainer );
			if( scontainerInfo != null && scontainerInfo.rejection != null && hasNonSContainer.get() ) {
				rejectedInfos.add( new RejectedSetDynamicInfo( scontainerInfo, receivedInfo ) );
				isRejected = true;
			}

			if( isRejected != true ) {
				List<SChangeInfo> schanges = null;
				if( rejectedInfos.isEmpty() != true ) {
					schanges = new ArrayList<>();
					final Iterator<RejectedSetDynamicInfo> iterator = rejectedInfos.iterator();
					while( iterator.hasNext() ) {
						final RejectedSetDynamicInfo rejectedInfo = iterator.next();
						if( RootController.isApplicable( origin, this, rejectedInfo.info.rejection ) ) {
							final SetDynamicInfo info = super.setDynamicInfo( origin, rejectedInfo.receivedInfo, false );
							if( info != null ) {
								schanges.add( info.schange );
							}
							iterator.remove();
						}
					}
				}

				final SetDynamicInfo info = super.setDynamicInfo( origin, receivedInfo, false );
				if( schanges != null ) {
					for( final SChangeInfo schange: schanges ) {
						triggerOnChange( origin, schange );
					}
				}
				if( scontainerInfo != null ) {
					triggerOnChange( origin, scontainerInfo.schange );
				}
				if( info != null ) {
					triggerOnChange( origin, info.schange );
				}
			}
		}
		return null;
	}

	@Override
	public void setAuthorizedRevision( final Controller origin, final AckInfo info, final boolean allowCompaction ) {
		super.setAuthorizedRevision( origin, info, false );
	}

	@Override
	protected Long checkIdleNotRequired( final ControllerIo io ) {
		return super.checkIdle( io );
	}

	@Override
	protected Long checkIdleRequired( final ControllerIo io ) {
		return null;
	}

	@Override
	public void run() {
		if( ! onDestroyed.get() ) {
			try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
				rejectedInfos.cleanup( SYNC_CLIENT_UPDATE_INTERVAL );
			}
			baggage.scheduler.schedule( this, SYNC_CLIENT_UPDATE_INTERVAL );
		}
	}
}
