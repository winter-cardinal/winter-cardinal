/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package method;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.AtomicDouble;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnShow;
import org.wcardinal.controller.annotation.Tracked;
import org.wcardinal.util.json.Json;
import org.wcardinal.util.reflection.AbstractCallableMethod;
import org.wcardinal.util.reflection.AbstractMethods;
import org.wcardinal.util.reflection.CallableMethods;
import org.wcardinal.util.reflection.LockRequirements;
import org.wcardinal.util.reflection.MethodAndOrders;
import org.wcardinal.util.reflection.MethodContainer;
import org.wcardinal.util.reflection.MethodHook;
import org.wcardinal.util.reflection.MethodResult;
import org.wcardinal.util.reflection.MethodWrapper;
import org.wcardinal.util.reflection.TrackingData;
import org.wcardinal.util.reflection.TrackingIds;
import org.wcardinal.util.reflection.VoidMethodAndOrders;
import org.wcardinal.util.reflection.VoidParametrizedMethods;
import org.wcardinal.util.reflection.VoidTypedParametrizedMethods;
import org.wcardinal.util.thread.Scheduler;
import org.wcardinal.util.thread.Unlocker;

class DummyMethodContainer implements MethodContainer {
	final Map<Object, Object> workingData = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <V> V getWorkingData( final Object key ) {
		return (V) workingData.get( key );
	}

	@Override
	public void putWorkingData(Object key, Object data) {
		workingData.put( key,  data );
	}

	@Override
	public TrackingIds getTrackingIds() {
		return null;
	}

	@Override
	public void setTrackingIds(TrackingIds ids) {
	}

	@Override
	public Unlocker lock() {
		return null;
	}

	@Override
	public void unlock() {
	}

	@Override
	public boolean isLockedByCurrentThread() {
		return false;
	}

	@Override
	public Long getRequestId() {
		return null;
	}

	@Override
	public void setRequestId(Long id) {
	}

	@Override
	public Scheduler getScheduler() {
		return null;
	}

	@Override
	public <T> void handle( final MethodResult<T> result ) {

	}
};

public class MethodTest {
	@OnChange( "foo" )
	void onChange() {}

	@OnChange( "foo" )
	static void onChangeStatic() {}

	@OnChange( "foo" ) @Tracked
	void onChangeTracked() {}

	@OnChange( "foo" ) @Tracked
	static void onChangeStaticTracked() {}

	@Test
	public void voidTypedParametrizedMethods1() throws NoSuchMethodException, SecurityException {
		final Method method = this.getClass().getDeclaredMethod( "onChange" );
		final Method methodStatic = this.getClass().getDeclaredMethod( "onChangeStatic" );
		final Method methodTracked = this.getClass().getDeclaredMethod( "onChangeTracked" );
		final Method methodStaticTracked = this.getClass().getDeclaredMethod( "onChangeStaticTracked" );
		final Set<Method> methods = new HashSet<>();
		methods.add( method );
		methods.add( methodStatic );
		methods.add( methodTracked );
		methods.add( methodStaticTracked );
		final VoidTypedParametrizedMethods voidTypedParametrizedMethods
			= VoidTypedParametrizedMethods.create(methods, OnChange.class);

		Assert.assertFalse( voidTypedParametrizedMethods.contains() );
		Assert.assertFalse( voidTypedParametrizedMethods.contains( "bar" ) );
		Assert.assertTrue( voidTypedParametrizedMethods.contains( "foo" ) );
		Assert.assertTrue( voidTypedParametrizedMethods.containsNonStatic() );
		Assert.assertFalse( voidTypedParametrizedMethods.containsLockUnspecified() );
		Assert.assertFalse( voidTypedParametrizedMethods.containsLockUnspecified( "bar" ) );
		Assert.assertTrue( voidTypedParametrizedMethods.containsLockUnspecified( "foo" ) );
		Assert.assertFalse( voidTypedParametrizedMethods.containsLockRequired() );
		Assert.assertFalse( voidTypedParametrizedMethods.containsLockRequired( "bar" ) );
		Assert.assertFalse( voidTypedParametrizedMethods.containsLockRequired( "foo" ) );
		Assert.assertFalse( voidTypedParametrizedMethods.containsLockNotRequired() );
		Assert.assertFalse( voidTypedParametrizedMethods.containsLockNotRequired( "bar" ) );
		Assert.assertFalse( voidTypedParametrizedMethods.containsLockNotRequired( "foo" ) );

		final MethodContainer container = new DummyMethodContainer();
		voidTypedParametrizedMethods.init( container );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingData( "foo", null, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingData( "foo", container, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingData( "foo", container, this) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataDefaults( null, null) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataDefaults( container, null) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataDefaults( container, this) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataNonDefaults( "foo", null, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingDataNonDefaults( "foo", container, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingDataNonDefaults( "foo", container, this) );
	}

	@Test
	public void voidTypedParametrizedMethods2() throws NoSuchMethodException, SecurityException {
		final Method method = this.getClass().getDeclaredMethod( "onChange" );
		final Method methodTracked = this.getClass().getDeclaredMethod( "onChangeTracked" );
		final Set<Method> methods = new HashSet<>();
		methods.add( method );
		methods.add( methodTracked );
		final VoidTypedParametrizedMethods voidTypedParametrizedMethods
			= VoidTypedParametrizedMethods.create(methods, OnChange.class);

		Assert.assertTrue( voidTypedParametrizedMethods.containsNonStatic() );
	}

	@Test
	public void voidTypedParametrizedMethods3() throws NoSuchMethodException, SecurityException {
		final Method methodStatic = this.getClass().getDeclaredMethod( "onChangeStatic" );
		final Method methodStaticTracked = this.getClass().getDeclaredMethod( "onChangeStaticTracked" );
		final Set<Method> methods = new HashSet<>();
		methods.add( methodStatic );
		methods.add( methodStaticTracked );
		final VoidTypedParametrizedMethods voidTypedParametrizedMethods
			= VoidTypedParametrizedMethods.create(methods, OnChange.class);

		Assert.assertFalse( voidTypedParametrizedMethods.containsNonStatic() );
	}

	@OnShow("*")
	void onShowDefault() {}

	@OnShow("*") @Tracked
	void onShowTrackedDefault() {}

	@OnShow("*")
	static void onShowStaticDefault() {}

	@OnShow("*") @Tracked
	static void onShowStaticTrackedDefault() {}

	@Test
	public void voidTypedParametrizedMethods4d() throws NoSuchMethodException, SecurityException {
		final Method method = this.getClass().getDeclaredMethod( "onShowDefault" );
		final Method methodTracked = this.getClass().getDeclaredMethod( "onShowTrackedDefault" );
		final Method methodStatic = this.getClass().getDeclaredMethod( "onShowStaticDefault" );
		final Method methodStaticTracked = this.getClass().getDeclaredMethod( "onShowStaticTrackedDefault" );
		final Set<Method> methods = new HashSet<>();
		methods.add( method );
		methods.add( methodTracked );
		methods.add( methodStatic );
		methods.add( methodStaticTracked );
		final VoidTypedParametrizedMethods voidTypedParametrizedMethods
			= VoidTypedParametrizedMethods.create(methods, OnShow.class);

		Assert.assertTrue( voidTypedParametrizedMethods.containsNonStatic() );

		final MethodContainer container = new DummyMethodContainer();
		voidTypedParametrizedMethods.init( container );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingData( "foo", null, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingData( "foo", container, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingData( "foo", container, this) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataDefaults( null, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingDataDefaults( container, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingDataDefaults( container, this) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataNonDefaults( "foo", null, null) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataNonDefaults( "foo", container, null) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataNonDefaults( "foo", container, this) );
	}

	@OnShow
	void onShow() {}

	@OnShow @Tracked
	void onShowTracked() {}

	@OnShow
	static void onShowStatic() {}

	@OnShow @Tracked
	static void onShowStaticTracked() {}

	@Test
	public void voidTypedParametrizedMethods4() throws NoSuchMethodException, SecurityException {
		final Method method = this.getClass().getDeclaredMethod( "onShow" );
		final Method methodTracked = this.getClass().getDeclaredMethod( "onShowTracked" );
		final Method methodStatic = this.getClass().getDeclaredMethod( "onShowStatic" );
		final Method methodStaticTracked = this.getClass().getDeclaredMethod( "onShowStaticTracked" );
		final Set<Method> methods = new HashSet<>();
		methods.add( method );
		methods.add( methodTracked );
		methods.add( methodStatic );
		methods.add( methodStaticTracked );
		final VoidTypedParametrizedMethods voidTypedParametrizedMethods
			= VoidTypedParametrizedMethods.create(methods, OnShow.class);

		Assert.assertTrue( voidTypedParametrizedMethods.containsNonStatic() );

		final MethodContainer container = new DummyMethodContainer();
		voidTypedParametrizedMethods.init( container );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingData( ".", null, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingData( ".", container, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingData( ".", container, this) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataDefaults( null, null) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataDefaults( container, null) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataDefaults( container, this) );
		Assert.assertNull( voidTypedParametrizedMethods.getTrackingDataNonDefaults( ".", null, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingDataNonDefaults( ".", container, null) );
		Assert.assertNotNull( voidTypedParametrizedMethods.getTrackingDataNonDefaults( ".", container, this) );
	}

	@Callable
	void callable() {}

	@Callable
	static void callableStatic() {}

	@Callable @Tracked
	void callableTracked() {}

	@Callable @Tracked
	static void callableStaticTracked() {}

	@Test
	public void callableMethods() throws NoSuchMethodException, SecurityException {
		final Method method = this.getClass().getDeclaredMethod( "callable" );
		final Method methodStatic = this.getClass().getDeclaredMethod( "callableStatic" );
		final Method methodTracked = this.getClass().getDeclaredMethod( "callableTracked" );
		final Method methodStaticTracked = this.getClass().getDeclaredMethod( "callableStaticTracked" );
		final Set<Method> methods = new HashSet<>();
		methods.add( method );
		methods.add( methodStatic );
		methods.add( methodTracked );
		methods.add( methodStaticTracked );
		final CallableMethods<Void> callableMethods
			= CallableMethods.create( methods, ResolvableType.forClass(MethodTest.class), null );

		Assert.assertFalse( callableMethods.containsTrackable( "callable" ) );
		Assert.assertFalse( callableMethods.containsTrackable( "bar" ) );
		Assert.assertFalse( callableMethods.containsLockNotRequired( "callable" ) );
		Assert.assertFalse( callableMethods.containsLockNotRequired( "bar" ) );
		Assert.assertFalse( callableMethods.containsLockRequired( "callable" ) );
		Assert.assertFalse( callableMethods.containsLockRequired( "bar" ) );
		Assert.assertTrue( callableMethods.containsLockUnspecified( "callable" ) );
		Assert.assertFalse( callableMethods.containsLockUnspecified( "bar" ) );

		final MethodContainer container = new DummyMethodContainer();
		callableMethods.init( container );
		Assert.assertNull( callableMethods.getTrackingData( "callable", null, null) );
		Assert.assertNull( callableMethods.getTrackingData( "callable", container, null) );
		Assert.assertNull( callableMethods.getTrackingData( "callable", container, this) );
		Assert.assertNull( callableMethods.getTrackingData( "bar", null, null) );
		Assert.assertNull( callableMethods.getTrackingData( "bar", container, null) );
		Assert.assertNull( callableMethods.getTrackingData( "bar", container, this) );
		Assert.assertNull( callableMethods.getTrackingData( "callableTracked", null, null) );
		Assert.assertNotNull( callableMethods.getTrackingData( "callableTracked", container, null) );
		Assert.assertNotNull( callableMethods.getTrackingData( "callableTracked", container, this) );
		Assert.assertNull( callableMethods.getTrackingData( "callableStaticTracked", null, null) );
		Assert.assertNotNull( callableMethods.getTrackingData( "callableStaticTracked", container, null) );
		Assert.assertNotNull( callableMethods.getTrackingData( "callableStaticTracked", container, this) );
	}

	@Test
	public void methodAndOrders() throws NoSuchMethodException, SecurityException {
		final Method method = this.getClass().getDeclaredMethod( "callable" );
		final Method methodStatic = this.getClass().getDeclaredMethod( "callableStatic" );
		final Set<Method> methods = new HashSet<>();
		methods.add( method );
		methods.add( methodStatic );
		final MethodAndOrders<Void> methodAndOrders
			= MethodAndOrders.create( methods, ResolvableType.forClass(MethodTest.class) );

		Assert.assertTrue( methodAndOrders.containsNonStatic() );
		Assert.assertFalse( methodAndOrders.call(null, null, null, LockRequirements.ANY, this).isEmpty() );
		Assert.assertTrue( methodAndOrders.call(null, null, null, LockRequirements.REQUIRED, this).isEmpty() );

		final MethodContainer container = new DummyMethodContainer();
		methodAndOrders.init( container );
		Assert.assertNull( methodAndOrders.getTrackingData(container, null) );
	}

	@Test
	public void voidMethodAndOrders() throws NoSuchMethodException, SecurityException {
		final Method method = this.getClass().getDeclaredMethod( "callable" );
		final Method methodStatic = this.getClass().getDeclaredMethod( "callableStatic" );
		final Set<Method> methods = new HashSet<>();
		methods.add( method );
		methods.add( methodStatic );
		final VoidMethodAndOrders voidMethodAndOrders
			= VoidMethodAndOrders.create( methods, ResolvableType.forClass(MethodTest.class) );

		Assert.assertTrue( voidMethodAndOrders.containsNonStatic() );
		voidMethodAndOrders.call(null, null, null, LockRequirements.ANY, this);
		voidMethodAndOrders.call(null, null, null, LockRequirements.REQUIRED, this);
	}

	@Test
	public void methodAndOrdersTracked() throws NoSuchMethodException, SecurityException {
		final Method method = this.getClass().getDeclaredMethod( "callable" );
		final Method methodStatic = this.getClass().getDeclaredMethod( "callableStatic" );
		final Method methodTracked = this.getClass().getDeclaredMethod( "callableTracked" );
		final Method methodStaticTracked = this.getClass().getDeclaredMethod( "callableStaticTracked" );
		final Set<Method> methods = new HashSet<>();
		methods.add( method );
		methods.add( methodStatic );
		methods.add( methodTracked );
		methods.add( methodStaticTracked );
		final MethodAndOrders<Void> methodAndOrders
			= MethodAndOrders.create( methods, ResolvableType.forClass(MethodTest.class) );

		final MethodContainer container = new DummyMethodContainer();
		methodAndOrders.init( container );
		Assert.assertNull( methodAndOrders.getTrackingData(null, null) );
		Assert.assertNotNull( methodAndOrders.getTrackingData(container, null) );
		Assert.assertNotNull( methodAndOrders.getTrackingData(container, this) );
	}

	@Test
	public void voidParametrizedMethods1() throws NoSuchMethodException, SecurityException {
		final Method method = this.getClass().getDeclaredMethod( "onChange" );
		final Method methodStatic = this.getClass().getDeclaredMethod( "onChangeStatic" );
		final Method methodTracked = this.getClass().getDeclaredMethod( "onChangeTracked" );
		final Method methodStaticTracked = this.getClass().getDeclaredMethod( "onChangeStaticTracked" );
		final Set<Method> methods = new HashSet<>();
		methods.add( method );
		methods.add( methodStatic );
		methods.add( methodTracked );
		methods.add( methodStaticTracked );
		final VoidParametrizedMethods voidParametrizedMethods
			= VoidParametrizedMethods.create(methods);

		final MethodContainer container = new DummyMethodContainer();
		voidParametrizedMethods.init( container );
		Assert.assertNull( voidParametrizedMethods.getTrackingData( null, null) );
		Assert.assertNotNull( voidParametrizedMethods.getTrackingData( container, null) );
		Assert.assertNotNull( voidParametrizedMethods.getTrackingData( container, this) );
	}

	@Test
	public void voidParametrizedMethods2() throws NoSuchMethodException, SecurityException {
		final Method method = this.getClass().getDeclaredMethod( "onChange" );
		final Method methodStatic = this.getClass().getDeclaredMethod( "onChangeStatic" );
		final Set<Method> methods = new HashSet<>();
		methods.add( method );
		methods.add( methodStatic );
		final VoidParametrizedMethods voidParametrizedMethods
			= VoidParametrizedMethods.create(methods);

		final MethodContainer container = new DummyMethodContainer();
		voidParametrizedMethods.init( container );
		voidParametrizedMethods.call(container, null, null, LockRequirements.ANY, null);
		voidParametrizedMethods.call(container, null, null, LockRequirements.ANY, this);
		voidParametrizedMethods.call(container, null, null, LockRequirements.REQUIRED, null);
		voidParametrizedMethods.call(container, null, null, LockRequirements.REQUIRED, this);
	}

	@Test
	public void methodWrapper() throws NoSuchMethodException, SecurityException {
		final Method methodTracked = this.getClass().getDeclaredMethod( "onChangeTracked" );
		final MethodWrapper<Void> wrapper = new MethodWrapper<Void>( methodTracked );
		class DummyMethodHook implements MethodHook {
			public boolean before = false;
			public boolean after = false;

			@Override
			public void before() {
				before = true;
			}

			@Override
			public void after() {
				after = true;
			}
		}
		final MethodContainer container = new DummyMethodContainer();
		wrapper.init( container );
		final TrackingData trackingData = new TrackingData();
		trackingData.put( wrapper, 0L );
		DummyMethodHook hook = new DummyMethodHook();
		wrapper.call(container, trackingData, hook, this, new Object[ 0 ]);
		Assert.assertTrue( hook.before );
		Assert.assertTrue( hook.after );
	}

	@Test
	public void toParameters() {
		final ArrayNode jsonParameters = Json.mapper.createArrayNode();
		jsonParameters.add( NullNode.instance );
		final Object[] parameters = AbstractMethods.toParameters(jsonParameters, 0);
		Assert.assertEquals( parameters.length, 1 );
		Assert.assertNull( parameters[ 0 ] );
	}

	private static <T, V> void castSuccess( Class<T> klass, Object value, V expected ) {
		final JavaType[] types = new JavaType[] { Json.mapper.constructType( klass ) };
		final Object[] parameters = new Object[] { value };
		final Object[] casted = AbstractCallableMethod.cast( types, parameters );
		Assert.assertNotNull( casted );
		Assert.assertEquals( casted.length, 1 );
		Assert.assertEquals( casted[ 0 ], expected );
	}

	private static <T> void castAsIs( Class<T> klass, Object value ) {
		castSuccess( klass, value, value );
	}

	private static <T> void castFail( Class<T> klass, Object value ) {
		final JavaType[] types = new JavaType[] { Json.mapper.constructType( klass ) };
		final Object[] parameters = new Object[] { value };
		final Object[] casted = AbstractCallableMethod.cast( types, parameters );
		Assert.assertNull( casted );
	}

	@Test
	public void castClass() {
		castAsIs( MethodTest.class, null );
	}

	@Test
	public void castBoolean() {
		castFail( boolean.class, null );
		castAsIs( Boolean.class, null );
		castAsIs( boolean.class, true );
		castAsIs( Boolean.class, true );
		castAsIs( boolean.class, false );
		castAsIs( Boolean.class, false );
		castFail( boolean.class, this );
		castFail( Boolean.class, this );
	}

	@Test
	public void castMismatch() {
		final JavaType[] types = new JavaType[] {};
		final Object[] parameters = new Object[] { null };
		final Object[] casted = AbstractCallableMethod.cast( types, parameters );
		Assert.assertNull( casted );
	}

	@Test
	public void castNumber() {
		castSuccess( Long.class, 0, 0L );
		castSuccess( long.class, 0, 0L );
		castSuccess( Integer.class, 0, 0 );
		castSuccess( int.class, 0, 0 );
		castSuccess( Short.class, 0, (short) 0 );
		castSuccess( short.class, 0, (short) 0 );
		castSuccess( Byte.class, 0, (byte) 0 );
		castSuccess( byte.class, 0, (byte) 0 );
		castSuccess( Float.class, 0, 0.0F );
		castSuccess( float.class, 0, 0.0F );
		castSuccess( Double.class, 0, 0.0 );
		castSuccess( double.class, 0, 0.0 );
	}

	@Test
	public void castDouble() {
		castFail( AtomicDouble.class, 0 );
		castFail( double.class, true );
		castFail( double.class, Json.mapper.createArrayNode() );
	}

	@Test
	public void castEnum() {
		castSuccess( TimeUnit.class, TimeUnit.DAYS.toString(), TimeUnit.DAYS );
	}

	@Test
	public void castNode() {
		castAsIs( ArrayNode.class, Json.mapper.createArrayNode() );
		castFail( ArrayNode.class, Json.mapper.createObjectNode() );
		castAsIs( ObjectNode.class, Json.mapper.createObjectNode() );
		castFail( ObjectNode.class, Json.mapper.createArrayNode() );
	}
}
