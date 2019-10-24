/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.ControllerAttributes;
import org.wcardinal.controller.ControllerContext;
import org.wcardinal.controller.ControllerIo;
import org.wcardinal.controller.Facade;
import org.wcardinal.controller.Factory;
import org.wcardinal.controller.PageFactory;
import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.annotation.CallableExceptionHandler;
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.DisplayName;
import org.wcardinal.controller.annotation.DisplayNameMessage;
import org.wcardinal.controller.annotation.NoPrimaryPage;
import org.wcardinal.controller.annotation.OnCheck;
import org.wcardinal.controller.annotation.OnNotice;
import org.wcardinal.controller.annotation.OnPostCreate;
import org.wcardinal.controller.annotation.OnHide;
import org.wcardinal.controller.annotation.OnIdleCheck;
import org.wcardinal.controller.annotation.OnRequest;
import org.wcardinal.controller.annotation.OnShow;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.annotation.Page;
import org.wcardinal.controller.annotation.Popup;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnDestroy;
import org.wcardinal.controller.annotation.ExceptionHandler;
import org.wcardinal.controller.annotation.Primary;
import org.wcardinal.controller.annotation.SharedComponent;
import org.wcardinal.controller.annotation.Task;
import org.wcardinal.controller.annotation.TaskExceptionHandler;
import org.wcardinal.controller.data.SClass;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.SMap;
import org.wcardinal.controller.data.SMovableList;
import org.wcardinal.controller.data.SNavigableMap;
import org.wcardinal.controller.data.SROQueue;
import org.wcardinal.controller.data.SString;
import org.wcardinal.controller.data.SArrayNode;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SDouble;
import org.wcardinal.controller.data.SJsonNode;
import org.wcardinal.controller.data.SFloat;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.SLong;
import org.wcardinal.controller.data.SObjectNode;
import org.wcardinal.controller.data.SQueue;
import org.wcardinal.controller.data.SVariable;
import org.wcardinal.controller.data.annotation.Descending;
import org.wcardinal.controller.data.internal.SGeneric;
import org.wcardinal.controller.data.internal.SNavigableMapImpl;
import org.wcardinal.controller.data.internal.SType;
import org.wcardinal.controller.internal.info.OnIdleCheckMethodInfo;
import org.wcardinal.controller.internal.info.StaticData;
import org.wcardinal.controller.internal.info.StaticInfo;
import org.wcardinal.controller.internal.info.StaticDataTask;
import org.wcardinal.controller.internal.info.StaticDataVariable;
import org.wcardinal.controller.scope.ControllerScopeAttributes;
import org.wcardinal.controller.scope.ControllerScopeAttributesHolder;
import org.wcardinal.exception.IllegalArgumentTypeException;
import org.wcardinal.exception.IllegalConstantException;
import org.wcardinal.exception.IllegalDecorationException;
import org.wcardinal.exception.IllegalFieldException;
import org.wcardinal.exception.IllegalModifierException;
import org.wcardinal.exception.IllegalReturnTypeException;
import org.wcardinal.exception.TaskOverloadException;
import org.wcardinal.exception.UnknownTargetException;
import org.wcardinal.util.reflection.AbstractTypedMethods;
import org.wcardinal.util.reflection.CallableMethods;
import org.wcardinal.util.reflection.LockRequirements;
import org.wcardinal.util.reflection.MethodResult;
import org.wcardinal.util.reflection.MethodResultData;
import org.wcardinal.util.reflection.TaskMethods;
import org.wcardinal.util.reflection.TrackingData;
import org.wcardinal.util.reflection.TypedExceptionHandlerMethods;
import org.wcardinal.util.reflection.MethodAndOrders;
import org.wcardinal.util.reflection.VoidParametrizedMethods;
import org.wcardinal.util.reflection.VoidMethodAndOrders;
import org.wcardinal.util.reflection.VoidTypedParametrizedMethods;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

public class ControllerFactory {
	static final Logger logger = LoggerFactory.getLogger(ControllerFactory.class);

	final ApplicationContext context;
	final ResolvableType type;
	final ControllerType controllerType;
	final Class<?> klass;
	final EnumSet<Property> properties;
	final String name;
	final String displayName;
	final String displayNameMessage;
	final boolean hasHistorical;
	final StaticInfo staticInfo;
	final Map<String, StaticDataVariable> variables = new HashMap<>();

	// OnXXX Methods
	final VoidParametrizedMethods onCreateMethods;
	final VoidParametrizedMethods onPostCreateMethods;
	final VoidParametrizedMethods onDestroyMethods;
	final VoidTypedParametrizedMethods onChangeMethods;
	final VoidTypedParametrizedMethods onShowMethods;
	final VoidTypedParametrizedMethods onHideMethods;
	final VoidTypedParametrizedMethods onTimeMethods;
	final VoidTypedParametrizedMethods onNoticeMethods;
	final VoidMethodAndOrders onRequestMethods;
	final MethodAndOrders<Boolean> onCheckMethods;
	final MethodAndOrders<Long> onIdleCheckMethods;
	final OnIdleCheckMethodInfo onIdleCheckMethodInfo;

	// Exception handlers
	final Set<String> exceptionTargets;
	final TypedExceptionHandlerMethods<Void> exceptionHandlers;

	// Tasks
	final TaskMethods<Object> tasks;
	final Map<String, Method> taskNameToMethod;
	final TypedExceptionHandlerMethods<String> taskExceptionHandlers;

	// Callables
	final CallableMethods<Object> callables;
	final Multimap<String, Method> callableNameToMethod;
	final TypedExceptionHandlerMethods<String> callableExceptionHandlers;

	// Fields
	final Map<String, ControllerFactoryFieldInfo> sscalars = new HashMap<>();
	final Map<String, ControllerFactoryFieldInfo> scontainers = new HashMap<>();
	final Map<String, ControllerFactoryControllerInfo> factories = new HashMap<>();
	final Map<String, ControllerFactoryControllerInfo> controllers = new HashMap<>();
	final String pagePrimary;
	final Set<Field> controllerContexts = new HashSet<>();
	final Map<Field, Field> facades = new HashMap<>();

	// Constant fields
	final HashSet<Field> constantFields = new HashSet<>();
	final HashSet<Field> constantStaticFields = new HashSet<>();
	final HashSet<Class<?>> constantClasses = new HashSet<>();

	public ControllerFactory( final ControllerType controllerType, final ApplicationContext context, final ResolvableType type, final EnumSet<Property> properties ){
		this( controllerType, context, type, properties, null, null, null );
	}

	public ControllerFactory( final ControllerType controllerType, final ApplicationContext context, final ResolvableType type, final EnumSet<Property> properties,
			final String name, final String displayName, final String displayNameMessage ){
		this.type = type;
		this.controllerType = controllerType;
		this.klass = type.getRawClass();
		this.name = ((name == null || name.length() <= 0) ? klass.getSimpleName() : name);
		this.displayName = displayName;
		this.displayNameMessage = displayNameMessage;
		this.properties = properties;
		this.context = context;

		// Constants
		final Constant constant = klass.getAnnotation(Constant.class);
		if( constant != null ){
			constantClasses.addAll( Arrays.asList( constant.value() ) );
		}

		// No primary flag
		final NoPrimaryPage noPrimaryPage = klass.getAnnotation(NoPrimaryPage.class);

		// Fields
		final AtomicReference<String> pagePrimary = new AtomicReference<String>( null );
		ReflectionUtils.doWithFields(klass, new FieldCallback(){
			@Override
			public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
				final Class<?> fieldType = field.getType();
				final EnumSet<Property> fieldProperties = Properties.create( properties, Properties.create( field ) );
				if( SVariable.class.isAssignableFrom(fieldType) ){
					checkConstantData( field );
					if( fieldType.isAssignableFrom(SBoolean.class) ){
						addSScalar( field, fieldProperties, SType.BOOLEAN, Boolean.class );
					} else if( fieldType.isAssignableFrom(SInteger.class) ){
						addSScalar( field, fieldProperties, SType.INTEGER, Integer.class );
					} else if( fieldType.isAssignableFrom(SLong.class) ){
						addSScalar( field, fieldProperties, SType.LONG, Long.class );
					} else if( fieldType.isAssignableFrom(SFloat.class) ){
						addSScalar( field, fieldProperties, SType.FLOAT, Float.class );
					} else if( fieldType.isAssignableFrom(SDouble.class) ){
						addSScalar( field, fieldProperties, SType.DOUBLE, Double.class );
					} else if( fieldType.isAssignableFrom(SJsonNode.class) ){
						if( fieldProperties.contains(Property.NON_NULL) && !fieldProperties.contains(Property.UNINITIALIZED) ) {
							fieldProperties.add( Property.UNINITIALIZED );
						}
						addSScalar( field, fieldProperties, SType.JSON_NODE, JsonNode.class );
					} else if( fieldType.isAssignableFrom(SArrayNode.class) ){
						addSScalar( field, fieldProperties, SType.ARRAY_NODE, ArrayNode.class );
					} else if( fieldType.isAssignableFrom(SObjectNode.class) ){
						addSScalar( field, fieldProperties, SType.OBJECT_NODE, ObjectNode.class );
					} else if( fieldType.isAssignableFrom(SString.class) ){
						addSScalar( field, fieldProperties, SType.STRING, String.class );
					} else if( fieldType.isAssignableFrom(SROQueue.class) ){
						fieldProperties.add( Property.READ_ONLY );
						addSContainer( field, fieldProperties, SType.RO_QUEUE, MethodTypeCheckerFactoryForSQueue.INSTANCE );
					} else if( fieldType.isAssignableFrom(SQueue.class) ){
						addSContainer( field, fieldProperties, SType.QUEUE, MethodTypeCheckerFactoryForSQueue.INSTANCE );
					} else if( fieldType.isAssignableFrom(SList.class) ){
						addSContainer( field, fieldProperties, SType.LIST, MethodTypeCheckerFactoryForSList.INSTANCE );
					} else if( fieldType.isAssignableFrom(SMovableList.class) ){
						addSContainer( field, fieldProperties, SType.MOVABLE_LIST, MethodTypeCheckerFactoryForSMovableList.INSTANCE );
					} else if( fieldType.isAssignableFrom(SClass.class) ){
						if( fieldProperties.contains(Property.NON_NULL) && !fieldProperties.contains(Property.UNINITIALIZED) ) {
							fieldProperties.add( Property.UNINITIALIZED );
						}
						addSScalar( field, fieldProperties, SType.CLASS, MethodTypeCheckerFactoryForSClass.INSTANCE );
					} else if( fieldType.isAssignableFrom(SMap.class) ){
						addSContainer( field, fieldProperties, SType.MAP, MethodTypeCheckerFactoryForSMap.INSTANCE );
					} else if( fieldType.isAssignableFrom(SNavigableMap.class) ){
						final boolean isDescending = field.isAnnotationPresent(Descending.class);
						final SType type = ( isDescending ? SType.DESCENDING_MAP : SType.ASCENDING_MAP );
						addSContainer( field, fieldProperties, type, MethodTypeCheckerFactoryForSNavigableMap.INSTANCE );
					}
				} else if( Factory.class.isAssignableFrom(fieldType) ){
					checkConstantController( field );
					if( fieldType.isAssignableFrom(ComponentFactory.class) ){
						if( checkComponentFactory( field ) ) {
							addFactory( field, fieldProperties, ControllerType.COMPONENT_FACTORY, ControllerType.COMPONENT, ResolvableType.forClass(ComponentFactoryImpl.class) );
						}
					} else if( fieldType.isAssignableFrom(PageFactory.class) ){
						if( checkPageFactory( field ) ) {
							addFactory( field, fieldProperties, ControllerType.PAGE_FACTORY, ControllerType.PAGE, ResolvableType.forClass(PageFactoryImpl.class) );
						}
					} else if( fieldType.isAssignableFrom(PopupFactory.class) ){
						if( checkPopupFactory( field ) ) {
							addFactory( field, fieldProperties, ControllerType.POPUP_FACTORY, ControllerType.POPUP, ResolvableType.forClass(PopupFactoryImpl.class) );
						}
					}
				} else if( fieldType.isAnnotationPresent(Page.class) ){
					checkConstantPage( field );
					if( checkPage( field ) ) {
						final String name = field.getName();
						final EnumSet<Property> typeProperties = Properties.create( fieldProperties, Properties.create( fieldType ) );
						final String displayName = findDisplayName( field, fieldType );
						final String displayNameMessage = findDisplayNameMessage( field, fieldType );
						final ControllerFactory factory = new ControllerFactory( ControllerType.PAGE, context, ResolvableType.forField(field, type), typeProperties, null, displayName, displayNameMessage );
						controllers.put( name, new ControllerFactoryControllerInfo( name, field, ControllerType.PAGE, fieldProperties, factory, null ));
						if( noPrimaryPage == null && (pagePrimary.get() == null || field.isAnnotationPresent(Primary.class)) ) pagePrimary.set( name );
					}
				} else if( fieldType.isAnnotationPresent(Popup.class) ){
					checkConstantPopup( field );
					if( checkPopup( field ) ) {
						final String name = field.getName();
						final EnumSet<Property> typeProperties = Properties.create( fieldProperties, Properties.create( fieldType ) );
						final String displayName = findDisplayName( field, fieldType );
						final String displayNameMessage = findDisplayNameMessage( field, fieldType );
						final ControllerFactory factory = new ControllerFactory( ControllerType.POPUP, context, ResolvableType.forField(field, type), typeProperties, null, displayName, displayNameMessage );
						if( field.isAnnotationPresent(Primary.class) ) {
							fieldProperties.add(Property.PRIMARY);
						}
						controllers.put( name, new ControllerFactoryControllerInfo( name, field, ControllerType.POPUP, fieldProperties, factory, null ));
					}
				} else if( fieldType.isAnnotationPresent(Component.class) ){
					checkConstantComponent( field );
					if( checkComponent( field ) ) {
						final String name = field.getName();
						final EnumSet<Property> typeProperties = Properties.create( fieldProperties, Properties.create( fieldType ) );
						final ControllerFactory factory = new ControllerFactory( ControllerType.COMPONENT, context, ResolvableType.forField(field, type), typeProperties );
						controllers.put( name, new ControllerFactoryControllerInfo( name, field, ControllerType.COMPONENT, fieldProperties, factory, null ));
					}
				} else if( fieldType.isAnnotationPresent(SharedComponent.class) ){
					checkConstantSharedComponent( field );
					if( checkSharedComponent( field ) ) {
						final String name = field.getName();
						final EnumSet<Property> typeProperties = Properties.create( fieldProperties, Properties.create( fieldType ) );
						typeProperties.add( Property.SHARED );
						final ControllerFactory factory = new ControllerFactory( ControllerType.SHARED_COMPONENT, context, ResolvableType.forField(field, type), typeProperties );
						controllers.put( name, new ControllerFactoryControllerInfo( name, field, ControllerType.SHARED_COMPONENT, fieldProperties, factory, null ));
					}
				} else if( Facade.class.isAssignableFrom(fieldType) ){
					checkConstantController( field );
					final Field controllerContext = ReflectionUtils.findField(fieldType, "controllerContext");
					if( controllerContext != null ) {
						ReflectionUtils.makeAccessible( controllerContext );
						ReflectionUtils.makeAccessible( field );
						facades.put( field, controllerContext );
					}
				} else if( fieldType.isAssignableFrom(ControllerContext.class) ) {
					checkConstantController( field );
					ReflectionUtils.makeAccessible( field );
					controllerContexts.add(field);
				} else if( field.isAnnotationPresent(Constant.class) ) {
					if( Modifier.isStatic( field.getModifiers() ) ) {
						constantStaticFields.add(field);
					} else {
						if( ! fieldProperties.contains(Property.FACTORY) ) {
							constantFields.add(field);
						} else {
							throw new IllegalConstantException( String.format( "@Constant is not supported on non-static fields of a factory type '%s'", field ) );
						}
					}
				}
			}
		});
		this.pagePrimary = pagePrimary.get();

		// Methods
		final Set<Method> onCreateMethods = new HashSet<>();
		final Set<Method> onPostCreateMethods = new HashSet<>();
		final Set<Method> onChangeMethods = new HashSet<>();
		final Set<Method> onShowMethods = new HashSet<>();
		final Set<Method> onHideMethods = new HashSet<>();
		final Set<Method> onDestroyMethods = new HashSet<>();
		final Set<Method> onTimeMethods = new HashSet<>();
		final Set<Method> onNoticeMethods = new HashSet<>();
		final Set<Method> onRequestMethods = new HashSet<>();
		final Set<Method> onCheckMethods = new HashSet<>();
		final Set<Method> onIdleCheckMethods = new HashSet<>();

		final Set<String> exceptionTargets = new HashSet<>();
		final Set<Method> exceptionHandlers = new HashSet<>();

		final Map<String, Method> taskNameToMethod = new HashMap<>();
		final Set<Method> taskExceptionHandlers = new HashSet<>();

		final Multimap<String, Method> callableNameToMethod = HashMultimap.create();
		final Set<Method> callableExceptionHandlers = new HashSet<>();

		final Method[] methods = ReflectionUtils.getUniqueDeclaredMethods( klass );
		for( final Method method: methods ) {
			if( AnnotationUtils.findAnnotation(method, OnCreate.class) != null ) {
				onCreateMethods.add( method );
				exceptionTargets.add( method.getName() );
			}

			if( AnnotationUtils.findAnnotation(method, OnPostCreate.class) != null ) {
				onPostCreateMethods.add( method );
				exceptionTargets.add( method.getName() );
			}

			if( AnnotationUtils.findAnnotation(method, OnDestroy.class) != null ) {
				if( checkMethodWithNoParameters( method ) ) {
					onDestroyMethods.add( method );
					exceptionTargets.add( method.getName() );
				}
			}

			if( AnnotationUtils.findAnnotation(method, OnChange.class) != null ) {
				if( checkOnChange( method ) ) {
					onChangeMethods.add( method );
					exceptionTargets.add( method.getName() );
				}
			}

			if( AnnotationUtils.findAnnotation(method, Task.class) != null ) {
				final String methodName = method.getName();
				if( taskNameToMethod.containsKey( methodName ) != true ) {
					taskNameToMethod.put( methodName, method );
					exceptionTargets.add( methodName );
				} else {
					if( checkTask( taskNameToMethod, methodName, method ) != true ) {
						throw new TaskOverloadException( String.format( "Task overload is not supported: '%s'", method.toGenericString() ) );
					}
				}
			}

			if( AnnotationUtils.findAnnotation(method, TaskExceptionHandler.class) != null ) {
				taskExceptionHandlers.add( method );
				exceptionTargets.add( method.getName() );
			}

			if( AnnotationUtils.findAnnotation(method, OnShow.class) != null ) {
				if( checkMethodWithOneParameter( method, ResolvableType.forClass(String.class) ) ) {
					onShowMethods.add( method );
					exceptionTargets.add( method.getName() );
				}
			}

			if( AnnotationUtils.findAnnotation(method, OnHide.class) != null ) {
				if( checkMethodWithOneParameter( method, ResolvableType.forClass(String.class) ) ) {
					onHideMethods.add( method );
					exceptionTargets.add( method.getName() );
				}
			}

			if( AnnotationUtils.findAnnotation(method, Callable.class) != null ) {
				final String methodName = method.getName();
				callableNameToMethod.put( methodName, method );
				exceptionTargets.add( methodName );
			}

			if( AnnotationUtils.findAnnotation(method, CallableExceptionHandler.class) != null ) {
				callableExceptionHandlers.add( method );
				exceptionTargets.add( method.getName() );
			}

			if( AnnotationUtils.findAnnotation(method, OnTime.class) != null ) {
				onTimeMethods.add( method );
				exceptionTargets.add( method.getName() );
			}

			if( AnnotationUtils.findAnnotation(method, OnNotice.class) != null ) {
				onNoticeMethods.add( method );
				exceptionTargets.add( method.getName() );
			}

			if( AnnotationUtils.findAnnotation(method, OnRequest.class) != null ) {
				if( checkOnRequest( method ) ) {
					onRequestMethods.add( method );
				}
			}

			if( AnnotationUtils.findAnnotation(method, OnCheck.class) != null ) {
				if( checkOnCheck( method ) ){
					onCheckMethods.add( method );
				}
			}

			if( AnnotationUtils.findAnnotation(method, OnIdleCheck.class) != null ) {
				if( checkOnIdleCheck( method ) ){
					onIdleCheckMethods.add( method );
				}
			}

			if( AnnotationUtils.findAnnotation(method, ExceptionHandler.class) != null ) {
				exceptionHandlers.add( method );
			}
		}

		// OnXXX Methods
		this.onCreateMethods = VoidParametrizedMethods.create( onCreateMethods );
		this.onPostCreateMethods = VoidParametrizedMethods.create( onPostCreateMethods );
		this.onDestroyMethods = VoidParametrizedMethods.create( onDestroyMethods );
		this.onChangeMethods = VoidTypedParametrizedMethods.create( onChangeMethods, OnChange.class );
		this.onShowMethods = VoidTypedParametrizedMethods.create( onShowMethods, OnShow.class );
		this.onHideMethods = VoidTypedParametrizedMethods.create( onHideMethods, OnHide.class );
		this.onTimeMethods = VoidTypedParametrizedMethods.create( onTimeMethods, OnTime.class, true );
		this.onNoticeMethods = VoidTypedParametrizedMethods.create( onNoticeMethods, OnNotice.class );
		this.onRequestMethods = VoidMethodAndOrders.create( onRequestMethods, type, ResolvableType.forClass(HttpServletRequest.class), ResolvableType.forClass(ControllerAttributes.class) );
		this.onCheckMethods = MethodAndOrders.<Boolean>create( onCheckMethods, type, ResolvableType.forClass(HttpServletRequest.class) );
		this.onIdleCheckMethods = MethodAndOrders.<Long>create( onIdleCheckMethods, type, ResolvableType.forClass(ControllerIo.class) );
		this.onIdleCheckMethodInfo = makeOnIdleCheckMethodInfo();

		// Exception handlers
		this.exceptionTargets = exceptionTargets;
		checkExceptionHandlers( exceptionHandlers );
		this.exceptionHandlers = TypedExceptionHandlerMethods.create( exceptionHandlers, type, ExceptionHandler.class );

		// Tasks
		this.tasks = TaskMethods.create( taskNameToMethod.values(), type );
		this.taskNameToMethod = taskNameToMethod;
		checkTaskExceptionHandlers( taskExceptionHandlers );
		this.taskExceptionHandlers = TypedExceptionHandlerMethods.create( taskExceptionHandlers, type, TaskExceptionHandler.class );

		// Callables
		this.callables = CallableMethods.create( callableNameToMethod.values(), type );
		this.callableNameToMethod = callableNameToMethod;
		checkCallableExceptionHandlers( callableExceptionHandlers );
		this.callableExceptionHandlers = TypedExceptionHandlerMethods.create( callableExceptionHandlers, type, CallableExceptionHandler.class );

		// Static info
		this.staticInfo = makeStaticInfo();

		// Has historical
		this.hasHistorical = hasHistorical();
	}

	<T> void addSScalar( final Field field, final EnumSet<Property> properties, final SType type, final Class<T> klass ) {
		final String name = field.getName();
		checkHistorical( properties, field );
		final MethodTypeChecker checker = new MethodTypeCheckerTwoStates( klass );
		final StaticDataVariable data = new StaticDataVariable( type.ordinal(), properties );
		sscalars.put( name, new ControllerFactoryFieldInfo( name, field, type, checker, data, properties, null ) );
		variables.put( name, data );
	}

	<T> void addSScalar( final Field field, final EnumSet<Property> properties, final SType type, final MethodTypeCheckerFactory factory ) {
		final String name = field.getName();
		checkHistorical( properties, field );
		final ResolvableType[] generics = ResolvableType.forField(field, this.type).getGenerics();
		final StaticDataVariable data = new StaticDataVariable( type.ordinal(), properties );
		sscalars.put( name, new ControllerFactoryFieldInfo( name, field, type, factory.create( generics ), data, properties, generics ) );
		variables.put( name, data );
	}

	<T> void addSContainer( final Field field, final EnumSet<Property> properties, final SType type, final MethodTypeCheckerFactory factory ) {
		final String name = field.getName();
		checkHistorical( properties, field );
		final ResolvableType[] generics = ResolvableType.forField(field, this.type).getGenerics();
		final StaticDataVariable data = new StaticDataVariable( type.ordinal(), properties );
		scontainers.put( name, new ControllerFactoryFieldInfo( name, field, type, factory.create( generics ), data, properties, generics ) );
		variables.put( name, data );
	}

	<T> void addFactory( final Field field, final EnumSet<Property> properties, final ControllerType type, final ControllerType childType, final ResolvableType resolvableType ) {
		final String name = field.getName();

		final ResolvableType[] generics = ResolvableType.forField(field, this.type).getGenerics();
		final EnumSet<Property> typeProperties = Properties.create( properties, Properties.create( generics[ 0 ].getRawClass() ) );
		typeProperties.add(Property.FACTORY);

		final ControllerFactory childFactory = new ControllerFactory( childType, context, generics[ 0 ], typeProperties );
		final ControllerFactory factory = new ControllerFactory( type, context, resolvableType, properties );

		factories.put( name, new ControllerFactoryControllerInfo( name, field, type, properties, factory, childFactory ) );
	}

	void checkHistorical( final EnumSet<Property> properties, final Field field ){
		if( properties.contains(Property.HISTORICAL) ) {
			if( properties.contains(Property.READ_ONLY) ){
				throw new IllegalDecorationException( String.format( "Read-only field '%s' must not be @Historical", field.toGenericString() ) );
			} else if( properties.contains(Property.SHARED) ) {
				throw new IllegalDecorationException( String.format( "Field '%s' in shared component can not be @Histrical", field.toGenericString() ) );
			}
		}
	}

	boolean checkTask( final Map<String, Method> taskNameToMethod, final String name, final Method target ) {
		final Method existing = taskNameToMethod.get( name );
		if( existing.isSynthetic() ) {
			if( target.isSynthetic() ) {
				return isSameMethod( target, existing );
			} else {
				taskNameToMethod.put( name, target );
				return true;
			}
		} else {
			if( target.isSynthetic() ) {
				return true;
			} else {
				return isSameMethod( target, existing );
			}
		}
	}

	boolean isSameMethod( final Method a, final Method b ) {
		final Class<?>[] ap = a.getParameterTypes();
		final Class<?>[] bp = b.getParameterTypes();
		if( ap.length != bp.length ) return false;

		for( int i=0; i<ap.length; ++i ) {
			final ResolvableType at = ResolvableType.forMethodParameter(new MethodParameter(a, i), type);
			final ResolvableType bt = ResolvableType.forMethodParameter(new MethodParameter(b, i), type);
			if( at.isAssignableFrom( bt ) != true || bt.isAssignableFrom( at ) != true ) {
				return false;
			}
		}

		return true;
	}

	boolean checkConstantData( final Field field ){
		return checkConstant( field, "the field of the type in the package org.wcardinal.controller.data" );
	}

	boolean checkConstantController( final Field field ){
		return checkConstant( field, "the field of the type in the package org.wcardinal.controller" );
	}

	boolean checkConstantPage( final Field field ){
		return checkConstant( field, "the field of the type annotated with the org.wcardinal.controller.annotation.Page" );
	}

	boolean checkConstantPopup( final Field field ){
		return checkConstant( field, "the field of the type annotated with the org.wcardinal.controller.annotation.Popup" );
	}

	boolean checkConstantComponent( final Field field ){
		return checkConstant( field, "the field of the type annotated with the org.wcardinal.controller.annotation.Component" );
	}

	boolean checkConstantSharedComponent( final Field field ){
		return checkConstant( field, "the field of the type annotated with the org.wcardinal.controller.annotation.SharedComponent" );
	}

	boolean checkConstant( final Field field, final String type ){
		if( field.isAnnotationPresent(Constant.class) ){
			throw new IllegalConstantException( String.format( "@Constant is not applicable to %s '%s'", type, field ) );
		}
		return true;
	}

	boolean checkOnCheck( final Method method ){
		if( Modifier.isStatic(method.getModifiers()) ) {
			if( method.getReturnType() == Boolean.TYPE ) {
				if( checkMethodWithOneParameter( method, ResolvableType.forClass(HttpServletRequest.class) ) ) {
					return true;
				}
			} else {
				throw new IllegalReturnTypeException( String.format( "@OnCheck method '%s' must have return type of boolean", method.toGenericString() ) );
			}
		} else {
			throw new IllegalModifierException( String.format( "@OnCheck method '%s' must be static", method.toGenericString() ) );
		}

		return false;
	}

	boolean checkOnIdleCheck( final Method method ){
		if( method.getReturnType() == Long.TYPE ) {
			if( checkMethodWithOneParameter( method, ResolvableType.forClass(ControllerIo.class) ) ) {
				return true;
			}
		} else {
			throw new IllegalReturnTypeException( String.format( "@OnIdleCheck method '%s' must have return type of long", method.toGenericString() ) );
		}

		return false;
	}

	boolean checkOnRequest( final Method method ){
		if( Modifier.isStatic(method.getModifiers()) ) {
			return checkMethodWithTwoParameters( method, ResolvableType.forClass(HttpServletRequest.class), ResolvableType.forClass(ControllerAttributes.class) );
		} else {
			throw new IllegalModifierException( String.format( "@OnRequest method '%s' must be static", method.toGenericString() ) );
		}
	}

	boolean checkSharedComponent( final Field field ){
		return checkController( "Shared component", field );
	}

	boolean checkComponent( final Field field ){
		return checkController( "Component", field );
	}

	boolean checkPage( final Field field ){
		return checkController( "Page", field );
	}

	boolean checkPopup( final Field field ){
		return checkController( "Popup", field );
	}

	boolean checkComponentFactory( final Field field ){
		return checkController( "Component factory", field );
	}

	boolean checkPageFactory( final Field field ){
		return checkController( "Page factory", field );
	}

	boolean checkPopupFactory( final Field field ){
		return checkController( "Popup factory", field );
	}

	boolean checkController( final String type, final Field field ){
		return checkFieldInSharedComponent( type, field );
	}

	boolean checkFieldInSharedComponent( final String type, final Field field ){
		if( properties.contains(Property.SHARED) != true ) return true;
		throw new IllegalFieldException( String.format( "%s '%s' must not be in shared component", type, field.toGenericString() ) );
	}

	boolean checkOnChange( final Method method ){
		return checkMethodType( method, MethodTypeCheckerType.ON_CHANGE, OnChange.class );
	}

	void checkTaskExceptionHandlers( final Collection<Method> handlers ){
		for( final Method handler: handlers ) {
			checkMethodType( handler, MethodTypeCheckerType.TASK, TaskExceptionHandler.class );
			MethodTypeCheckerForTaskExceptionHandler.INSTANCE.check( handler, type );
		}
	}

	void checkCallableExceptionHandlers( final Collection<Method> handlers ){
		for( final Method handler: handlers ) {
			checkMethodType( handler, MethodTypeCheckerType.CALLABLE, CallableExceptionHandler.class );
			MethodTypeCheckerForCallableExceptionHandler.INSTANCE.check( handler, type );
		}
	}

	void checkExceptionHandlers( final Collection<Method> handlers ){
		for( final Method handler: handlers ) {
			checkMethodType( handler, MethodTypeCheckerType.EXCEPTION, ExceptionHandler.class );
			MethodTypeCheckerForExceptionHandler.INSTANCE.check( handler, type );
		}
	}

	boolean checkMethodType( final Method method, final MethodTypeCheckerType type, final Class<? extends Annotation> annotationClass ){
		final String[] values = (String[]) AnnotationUtils.getValue(AnnotationUtils.findAnnotation(method, annotationClass));
		for( final MethodTypeChecker checker: findMethodTypeCheckers( method, values, type ) ){
			checker.check(method, this.type);
		}
		return true;
	}

	Collection<MethodTypeChecker> findMethodTypeCheckers( final Method method, final String[] values, final MethodTypeCheckerType type ){
		final Collection<MethodTypeChecker> result = new ArrayList<>();
		for( final String value: values ){
			if( value == null || Objects.equals( AbstractTypedMethods.TYPE_ALL, value ) || Objects.equals( AbstractTypedMethods.TYPE_SELF, value ) ) continue;
			final String[] path = value.trim().split("\\.");
			final ControllerFactory factory = findFieldFactory( path );
			if( factory != null ) {
				final MethodTypeChecker checker = factory.findMethodTypeChecker( type, path[ path.length - 1 ] );
				if( checker != null ) {
					result.add( checker );
					continue;
				}
			}

			throw new UnknownTargetException( String.format( "%s method '%s' has unknown target '%s'", toMethodTypeCheckerName( type ), method.toGenericString(), value ) );
		}
		return result;
	}

	ControllerFactory findFieldFactory( final String[] path ){
		ControllerFactory result = this;
		for( int i=0; i<path.length-1; ++i ){
			result = result.findFieldFactory( path[ i ] );
			if( result == null ) return null;
		}

		return result;
	}

	ControllerFactory findFieldFactory( final String name ){
		final ControllerFactoryControllerInfo info = controllers.get( name );
		if( info != null && info.childFactory == null ) {
			return info.factory;
		}
		return null;
	}

	String toMethodTypeCheckerName( final MethodTypeCheckerType type ){
		switch( type ){
		case TASK:
			return "@TaskExceptionHandler";
		case CALLABLE:
			return "@CallableExceptionHandler";
		case EXCEPTION:
			return "@ExceptionHandler";
		default:
			return "@OnChange";
		}
	}

	MethodTypeChecker findMethodTypeChecker( final MethodTypeCheckerType type, final String name ){
		switch( type ){
		case TASK:
			return ( taskNameToMethod.containsKey( name ) ? MethodTypeCheckerEmpty.INSTANCE : null );
		case CALLABLE:
			return ( callableNameToMethod.containsKey( name ) ? MethodTypeCheckerEmpty.INSTANCE : null );
		case EXCEPTION:
			return ( exceptionTargets.contains( name ) ? MethodTypeCheckerEmpty.INSTANCE : null );
		default:
			final ControllerFactoryFieldInfo sscalarInfo = sscalars.get( name );
			if( sscalarInfo != null ) {
				return sscalarInfo.checker;
			} else {
				final ControllerFactoryFieldInfo scontainerInfo = scontainers.get( name );
				if( scontainerInfo != null ) {
					return scontainerInfo.checker;
				} else {
					return null;
				}
			}
		}
	}

	boolean checkMethodWithNoParameters( final Method method ){
		if( method.getParameterTypes().length <= 0 ) return true;
		throw new IllegalArgumentTypeException( String.format( "Expecting () as parameter list of '%s'", method.toGenericString() ) );
	}

	boolean checkMethodWithOneParameter( final Method method, final ResolvableType type ){
		final Class<?>[] types = method.getParameterTypes();
		if( types.length <= 0 ) return true;
		if( types.length == 1 ) {
			final ResolvableType type0 = ResolvableType.forMethodParameter(new MethodParameter(method, 0), this.type);
			if( type0.isAssignableFrom(type) ) return true;
		}
		throw new IllegalArgumentTypeException( String.format( "Expecting (A) or () as parameter list of '%s' where A = '%s'", method.toGenericString(), type ) );
	}

	boolean checkMethodWithTwoParameters( final Method method, final ResolvableType typeA, final ResolvableType typeB ){
		final Class<?>[] types = method.getParameterTypes();
		if( types.length <= 0 ) return true;
		if( types.length == 1 ) {
			final ResolvableType type0 = ResolvableType.forMethodParameter(new MethodParameter(method, 0), type);
			if( type0.isAssignableFrom(typeA) || type0.isAssignableFrom(typeB) ) return true;
		}
		if( types.length == 2 ) {
			final ResolvableType type0 = ResolvableType.forMethodParameter(new MethodParameter(method, 0), type);
			final ResolvableType type1 = ResolvableType.forMethodParameter(new MethodParameter(method, 1), type);
			if( type0.isAssignableFrom(typeA) ) {
				if( type1.isAssignableFrom(typeB) ) {
					return true;
				}
			} else if( type0.isAssignableFrom(typeB) ) {
				if( type1.isAssignableFrom(typeA) ) {
					return true;
				}
			}
		}
		throw new IllegalArgumentTypeException( String.format( "Expecting (A, B) or its variants as parameter list of '%s' where A = '%s' and B = '%s'", method.toGenericString(), typeA, typeB ) );
	}

	StaticInfo makeStaticInfo(){
		final StaticInfo result = new StaticInfo( controllerType, properties );

		// Constatns
		for( final Field field: constantStaticFields ) {
			ReflectionUtils.makeAccessible( field );
			try {
				final String name = field.getName();
				final Object value = field.get(null);
				result.constants.put( name, value );
			} catch ( final Exception e ) {

			}
		}

		for( final Class<?> klass: constantClasses ) {
			final String name = klass.getSimpleName();
			if( klass.isEnum() ) {
				final Map<String, Object> enumMap = new HashMap<>();
				for( final Object enumConstant: klass.getEnumConstants() ) {
					enumMap.put(enumConstant.toString(), enumConstant);
				}
				result.constants.put( name, enumMap );
			} else {
				result.constants.put( name, klass );
			}
		}

		// Callables
		result.putAllData(callables.toDataMap());

		// Task
		result.putAllData(tasks.toDataMap());

		// Variables
		result.putAllData(variables);

		// Controllers
		for( final ControllerFactoryControllerInfo info: controllers.values() ){
			result.put( info.name, info.factory.staticInfo );
		}

		// Factories
		for( final ControllerFactoryControllerInfo info: factories.values() ) {
			result.put( info.name, info.factory.staticInfo );
		}

		return result;
	}

	OnIdleCheckMethodInfo makeOnIdleCheckMethodInfo(){
		boolean hasLockRequired = false;
		boolean hasLockNotRequired = false;

		hasLockRequired |= ( onIdleCheckMethods.containsLockUnspecified() || onIdleCheckMethods.containsLockRequired() );
		hasLockNotRequired |= onIdleCheckMethods.containsLockNotRequired();

		for( final ControllerFactoryControllerInfo info: controllers.values() ) {
			final OnIdleCheckMethodInfo other = info.factory.onIdleCheckMethodInfo;
			if( info.factory.properties.contains( Property.SHARED ) ) {
				hasLockNotRequired |= ( other.hasLockRequired || other.hasLockNotRequired );
			} else {
				hasLockRequired |= ( other.hasLockRequired );
				hasLockNotRequired |= ( other.hasLockNotRequired );
			}
		}

		for( final ControllerFactoryControllerInfo info: factories.values() ) {
			final OnIdleCheckMethodInfo other = info.childFactory.onIdleCheckMethodInfo;
			hasLockRequired |= ( other.hasLockRequired );
			hasLockNotRequired |= ( other.hasLockNotRequired );
		}

		return new OnIdleCheckMethodInfo( hasLockRequired, hasLockNotRequired );
	}

	boolean hasHistorical(){
		if( staticInfo.nameToData != null ) {
			for( final StaticData data: staticInfo.nameToData.values() ){
				if( data instanceof StaticDataVariable ){
					if( ((StaticDataVariable)data).properties.contains(Property.HISTORICAL) ) return true;
				} else if( data instanceof StaticDataTask ){
					if( ((StaticDataTask)data).properties.contains(Property.HISTORICAL) ) return true;
				}
			}
		}

		// Controllers
		for( final ControllerFactoryControllerInfo info: controllers.values() ) {
			switch( info.type ) {
			case COMPONENT:
				if( info.factory.hasHistorical ) {
					return true;
				}
				break;
			case PAGE:
				return true;
			case POPUP:
				return true;
			default:
				break;
			}
		}

		// Factories
		for( final ControllerFactoryControllerInfo info: factories.values() ) {
			switch( info.type ) {
			case COMPONENT_FACTORY:
				if( info.childFactory.hasHistorical ) {
					return true;
				}
				break;
			case PAGE_FACTORY:
				return true;
			case POPUP_FACTORY:
				return true;
			default:
				break;
			}
		}

		return false;
	}

	public String getName(){
		return name;
	}

	String findDisplayName( final Field field, final Class<?> type ){
		final DisplayName displayName = AnnotationUtils.findAnnotation(field, DisplayName.class);
		if( displayName != null ){
			return displayName.value();
		}

		final DisplayName displayNameOnType = AnnotationUtils.findAnnotation(type, DisplayName.class);
		if( displayNameOnType != null ){
			return displayNameOnType.value();
		}

		return null;
	}

	String findDisplayNameMessage( final Field field, final Class<?> type ){
		final DisplayNameMessage displayNameMessage = AnnotationUtils.findAnnotation(field, DisplayNameMessage.class);
		if( displayNameMessage != null ){
			return displayNameMessage.value();
		}

		final DisplayNameMessage displayNameMessageOnType = AnnotationUtils.findAnnotation(type, DisplayNameMessage.class);
		if( displayNameMessageOnType != null ){
			return displayNameMessageOnType.value();
		}

		return null;
	}

	public RootController createRoot( final ControllerBaggage baggage ) {
		ControllerScopeAttributesHolder.set(baggage.scopeAttributes);
		final AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		final Object instance = factory.createBean(klass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		postBeanCreationProcess(instance);
		ControllerScopeAttributesHolder.remove();
		return new RootController( name, this, instance, baggage );
	}

	public PageController createPage( final String name, final Controller parent, final Object instance,
			final ControllerBaggage baggage, final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks ) {
		postBeanCreationProcess(instance);
		return new PageController( name, parent, this, instance, baggage, null, lock, tasks );
	}

	public PopupController createPopup( final String name, final Controller parent, final Object instance,
			final ControllerBaggage baggage, final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks, final boolean isShown ) {
		postBeanCreationProcess(instance);
		return new PopupController( name, parent, this, instance, baggage, null, lock, tasks, isShown );
	}

	public ComponentController createComponent( final String name, final Controller parent, final Object instance,
			final ControllerBaggage baggage, final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks ) {
		postBeanCreationProcess(instance);
		return new ComponentController( name, parent, this, instance, baggage, null, lock, tasks );
	}

	public ComponentController createSharedComponent( final String name, final Controller parent, final Object instance,
			final ControllerBaggage baggage ) {
		postBeanCreationProcess(instance);
		return SharedComponentController.create( name, parent, this, instance, baggage, null );
	}

	public FactoryController<?> createFactory( final ControllerType type, final String name, final Controller parent, final Object instance,
			final ControllerBaggage baggage, final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks, final ControllerFactory childFactory ) {
		postBeanCreationProcess(instance);
		switch( type ) {
		case PAGE_FACTORY:
			return new PageFactoryController( name, parent, this, instance, baggage, lock, tasks, childFactory );
		case POPUP_FACTORY:
			return new PopupFactoryController( name, parent, this, instance, baggage, lock, tasks, childFactory );
		case COMPONENT_FACTORY:
		default:
			return new ComponentFactoryController( name, parent, this, instance, baggage, lock, tasks, childFactory );
		}
	}

	public ComponentController createDynamicComponent( final String name, final Controller parent, final ControllerBaggage baggage,
			final ArrayNode factoryParameters, final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks ) {
		ControllerScopeAttributesHolder.set(baggage.scopeAttributes);
		final AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		final Object instance = factory.createBean(klass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		postBeanCreationProcess(instance);
		ControllerScopeAttributesHolder.remove();
		return new ComponentController( name, parent, this, instance, baggage, factoryParameters, lock, tasks );
	}

	public PageController createDynamicPage( final String name, final Controller parent, final ControllerBaggage baggage,
			final ArrayNode factoryParameters, final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks ) {
		ControllerScopeAttributesHolder.set(baggage.scopeAttributes);
		final AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		final Object instance = factory.createBean(klass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		postBeanCreationProcess(instance);
		ControllerScopeAttributesHolder.remove();
		return new PageController( name, parent, this, instance, baggage, factoryParameters, lock, tasks );
	}

	public PopupController createDynamicPopup( final String name, final Controller parent, final ControllerBaggage baggage,
			final ArrayNode factoryParameters, final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks ) {
		ControllerScopeAttributesHolder.set(baggage.scopeAttributes);
		final AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		final Object instance = factory.createBean(klass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		postBeanCreationProcess(instance);
		ControllerScopeAttributesHolder.remove();
		return new PopupController( name, parent, this, instance, baggage, factoryParameters, lock, tasks, false );
	}

	public void destroy( final RootController controller ){
		ControllerScopeAttributes attributes = controller.baggage.scopeAttributes;
		ControllerScopeAttributesHolder.set( attributes );
		_destroy( controller );
		ControllerScopeAttributesHolder.remove();
		attributes.destroy();
	}

	public void destroy( final Controller controller ){
		ControllerScopeAttributesHolder.set(controller.baggage.scopeAttributes);
		_destroy( controller );
		ControllerScopeAttributesHolder.remove();
	}

	private void _destroy( final Controller controller ){
		final AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();

		// Controller
		for( final Controller target: controller.controllerNameToController.values() ){
			if( target instanceof SharedComponentController ) continue;
			_destroy( target );
		}

		// Controller
		factory.destroyBean(controller.instance);
	}

	void postBeanCreationProcess( final Object instance, final ControllerFactoryFieldInfo info ) {
		// Navigable map types
		if( info.type == SType.ASCENDING_MAP || info.type == SType.DESCENDING_MAP ) {
			final Field field = info.field;
			try {
				ReflectionUtils.makeAccessible( field );
				final Object value = field.get( instance );
				if( value instanceof SNavigableMapImpl<?> ){
					final SNavigableMapImpl<?> map = (SNavigableMapImpl<?>) value;
					if( info.type == SType.ASCENDING_MAP ) {
						map.setAscendingComparator();
					} else {
						map.setDescendingComparator();
					}
				}
			} catch ( final Exception e ) {

			}
		}

		// Generics types
		if( info.generics != null ) {
			final Field field = info.field;
			try {
				ReflectionUtils.makeAccessible( field );
				final Object value = field.get( instance );
				if( value instanceof SGeneric ){
					((SGeneric)value).setGenericType( info.generics[ 0 ] );
				}
			} catch ( final Exception e ) {

			}
		}
	}

	void postBeanCreationProcess( final Object instance ){
		for( final ControllerFactoryFieldInfo info: sscalars.values() ){
			postBeanCreationProcess( instance, info );
		}

		for( final ControllerFactoryFieldInfo info: scontainers.values() ){
			postBeanCreationProcess( instance, info );
		}
	}

	public void onRequest( final HttpServletRequest request, final ControllerBaggage baggage ){
		final VoidMethodAndOrders methods = onRequestMethods;
		final TrackingData trackingData = methods.getTrackingData(null, null);
		methods.call(null, trackingData, null, LockRequirements.ANY, null, request, baggage.attributes);
	}

	public boolean onCheck( final HttpServletRequest request ){
		final MethodAndOrders<Boolean> methods = onCheckMethods;
		final TrackingData trackingData = methods.getTrackingData(null, null);
		final Collection<MethodResult<Boolean>> results = methods.call(null, trackingData, null, LockRequirements.ANY, null, request);
		for( final MethodResult<Boolean> result: results ){
			if( result instanceof MethodResultData ) {
				final MethodResultData<Boolean> data = (MethodResultData<Boolean>) result;
				if( data.data != true ) return false;
			}
		}
		return true;
	}
}
