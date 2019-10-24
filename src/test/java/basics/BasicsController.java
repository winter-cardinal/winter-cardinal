/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableSet;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnDestroy;
import org.wcardinal.controller.annotation.ReadOnly;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SArrayNode;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SClass;
import org.wcardinal.controller.data.SDouble;
import org.wcardinal.controller.data.SFloat;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.SJsonNode;
import org.wcardinal.controller.data.SLong;
import org.wcardinal.controller.data.SObjectNode;
import org.wcardinal.controller.data.SString;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.controller.data.annotation.Uninitialized;
import org.wcardinal.exception.NotReadyException;
import org.wcardinal.util.thread.Unlocker;

@Controller
public class BasicsController extends AbstractController {
	final static Logger logger = LoggerFactory.getLogger(BasicsController.class);

	//---------------------------------------------
	// ARRAY NODE
	//---------------------------------------------
	@Autowired
	SArrayNode field_array_node;
	@Callable
	ArrayNode arrayNodeGet(){
		return field_array_node.get();
	}
	@Callable
	ArrayNode arrayNodeSet(){
		field_array_node.create().add( 128 );
		return field_array_node.get();
	}
	@Callable
	ArrayNode arrayNodeCompareAndSet( final ArrayNode target ){
		field_array_node.compareAndSet(field_array_node.get(), target);
		return field_array_node.get();
	}
	@Callable
	ArrayNode arrayNodeCompareAndSetFail( final ArrayNode target ){
		field_array_node.compareAndSet(target, target);
		return field_array_node.get();
	}
	@Callable
	ArrayNode arrayNodeGetAndSet( final ArrayNode target ){
		return field_array_node.getAndSet( target );
	}
	@Callable
	boolean arrayNodeEquals( final ArrayNode target ){
		return field_array_node.equals( field_array_node ) && field_array_node.equals( field_array_node.get() ) && field_array_node.equals( target );
	}
	@Callable
	boolean arrayNodeCompareTo(){
		return true;
	}
	@Callable
	boolean arrayNodeToString( final ArrayNode value ){
		return Objects.toString(value).equals(field_array_node.toString());
	}
	@Callable
	boolean arrayNodeTryLock(){
		if( field_array_node.tryLock() ) {
			try {
				return true;
			} finally {
				field_array_node.unlock();
			}
		}
		return false;
	}
	@Callable
	boolean arrayNodeTryLockTimeout(){
		if( field_array_node.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_array_node.unlock();
			}
		}
		return false;
	}

	// NON-NULL
	@Autowired @NonNull
	SArrayNode field_array_node_nonnull;
	@Callable
	ArrayNode arrayNodeNonnullSet( final ArrayNode value ){
		return field_array_node_nonnull.set( value );
	}
	@Callable
	boolean arrayNodeNonnullSetNull(){
		try {
			field_array_node_nonnull.set( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean arrayNodeNonnullCompareAndSet( final ArrayNode expected, final ArrayNode update ){
		return field_array_node_nonnull.compareAndSet( expected, update );
	}
	@Callable
	boolean arrayNodeNonnullCompareAndSetNullExpected( final ArrayNode expected ){
		try {
			field_array_node_nonnull.compareAndSet( expected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean arrayNodeNonnullCompareAndSetNullUnexpected( final ArrayNode unexpected ){
		try {
			field_array_node_nonnull.compareAndSet( unexpected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	ArrayNode arrayNodeNonnullGetAndSet( final ArrayNode value ){
		return field_array_node_nonnull.getAndSet( value );
	}
	@Callable
	boolean arrayNodeNonnullGetAndSetNull(){
		try {
			field_array_node_nonnull.getAndSet( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}

	// UNINITIALIZED
	@Autowired @Uninitialized
	SArrayNode field_array_node_uninitialized;
	@Callable
	boolean arrayNodeUninitializedIsInitializedBefore(){
		return (
			field_array_node_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean arrayNodeUninitializedGet(){
		return (
			field_array_node_uninitialized.get() == null &&
			field_array_node_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean arrayNodeUninitializedGetValue(){
		return (
			field_array_node_uninitialized.getValue() == null &&
			field_array_node_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean arrayNodeUninitializedLock(){
		boolean result = false;
		try( Unlocker unlocker = field_array_node_uninitialized.lock() ){
			result = field_array_node_uninitialized.isLocked();
		}
		return result == true && field_array_node_uninitialized.isInitialized() == false;
	}
	@Callable
	void arrayNodeUninitializedInitialize( final ArrayNode value ){
		field_array_node_uninitialized.set( value );
	}
	@Callable
	boolean arrayNodeUninitializedIsInitializedAfter(){
		return (
			field_array_node_uninitialized.isInitialized() == true
		);
	}
	@Callable
	boolean arrayNodeUninitializedInitializeAfter(){
		field_array_node_uninitialized.initialize();
		return field_array_node_uninitialized.isInitialized();
	}

	// UNINITIALIZED 2
	@Autowired @Uninitialized
	SString field_array_node_uninitialized2;
	@Callable
	boolean arrayNodeUninitialized2Initialize(){
		field_array_node_uninitialized2.initialize();
		return field_array_node_uninitialized2.isInitialized();
	}

	// DIRTY
	@Autowired
	SArrayNode field_array_node_dirty;
	@OnCreate
	void initArrayNodeDirty(){
		field_array_node_dirty.create().add( 1 );
	}
	@OnChange( "field_array_node_dirty" )
	void onArrayNodeDirtyChange( final ArrayNode newNode, final ArrayNode oldNode ){
		field_array_node_dirty.get().add( 3 );
		field_array_node_dirty.toDirty();
	}

	//---------------------------------------------
	// BOOLEAN
	//---------------------------------------------
	@Autowired
	SBoolean field_boolean;
	@Callable
	boolean booleanGet(){
		return field_boolean.get();
	}
	@Callable
	boolean booleanSet(){
		field_boolean.setValue( false );
		return field_boolean.get();
	}
	@Callable
	boolean booleanCompareAndSet( final boolean target ){
		field_boolean.compareAndSet(field_boolean.get(), target);
		return field_boolean.get();
	}
	@Callable
	boolean booleanCompareAndSetFail( final boolean target ){
		field_boolean.compareAndSet(target, target);
		return field_boolean.get();
	}
	@Callable
	boolean  booleanGetAndSet( final boolean target ){
		return field_boolean.getAndSet( target );
	}
	@Callable
	boolean booleanEquals( final boolean target ){
		return field_boolean.equals( field_boolean ) && field_boolean.equals( field_boolean.get() ) && field_boolean.equals( target );
	}
	@Callable
	boolean booleanCompareTo(){
		return true;
	}
	@Callable
	boolean booleanToString( final boolean value ){
		return Objects.toString(value).equals(field_boolean.toString());
	}
	@Callable
	boolean booleanTryLock(){
		if( field_boolean.tryLock() ) {
			try {
				return true;
			} finally {
				field_boolean.unlock();
			}
		}
		return false;
	}
	@Callable
	boolean booleanTryLockTimeout(){
		if( field_boolean.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_boolean.unlock();
			}
		}
		return false;
	}

	// NON-NULL
	@Autowired @NonNull
	SBoolean field_boolean_nonnull;
	@Callable
	Boolean booleanNonnullSet( final Boolean value ){
		return field_boolean_nonnull.set( value );
	}
	@Callable
	boolean booleanNonnullSetNull(){
		try {
			field_boolean_nonnull.set( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean booleanNonnullCompareAndSet( final Boolean expected, final Boolean update ){
		return field_boolean_nonnull.compareAndSet( expected, update );
	}
	@Callable
	boolean booleanNonnullCompareAndSetNullExpected( final Boolean expected ){
		try {
			field_boolean_nonnull.compareAndSet( expected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean booleanNonnullCompareAndSetNullUnexpected( final Boolean unexpected ){
		try {
			field_boolean_nonnull.compareAndSet( unexpected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	Boolean booleanNonnullGetAndSet( final Boolean value ){
		return field_boolean_nonnull.getAndSet( value );
	}
	@Callable
	boolean booleanNonnullGetAndSetNull(){
		try {
			field_boolean_nonnull.getAndSet( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}

	// UNINITIALIZED
	@Autowired @Uninitialized
	SBoolean field_boolean_uninitialized;
	@Callable
	boolean booleanUninitializedIsInitializedBefore(){
		return (
			field_boolean_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean booleanUninitializedGet(){
		return (
			field_boolean_uninitialized.get() == null &&
			field_boolean_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean booleanUninitializedGetValue(){
		return (
			field_boolean_uninitialized.getValue() == null &&
			field_boolean_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean booleanUninitializedLock(){
		boolean result = false;
		try( Unlocker unlocker = field_boolean_uninitialized.lock() ){
			result = field_boolean_uninitialized.isLocked();
		}
		return result == true && field_boolean_uninitialized.isInitialized() == false;
	}
	@Callable
	void booleanUninitializedInitialize( final Boolean value ){
		field_boolean_uninitialized.set( value );
	}
	@Callable
	boolean booleanUninitializedIsInitializedAfter(){
		return (
			field_boolean_uninitialized.isInitialized() == true
		);
	}
	@Callable
	boolean booleanUninitializedInitializeAfter(){
		field_boolean_uninitialized.initialize();
		return field_boolean_uninitialized.isInitialized();
	}

	// UNINITIALIZED 2
	@Autowired @Uninitialized
	SString field_boolean_uninitialized2;
	@Callable
	boolean booleanUninitialized2Initialize(){
		field_boolean_uninitialized2.initialize();
		return field_boolean_uninitialized2.isInitialized();
	}

	//---------------------------------------------
	// CLASS
	//---------------------------------------------
	@Autowired
	SClass<String> field_class;
	@Callable
	String classGet(){
		return field_class.get();
	}
	@Callable
	String classSet(){
		field_class.set( "b" );
		return field_class.get();
	}
	@Callable
	String classCompareAndSet( final String target ){
		field_class.compareAndSet(field_class.get(), target);
		return field_class.get();
	}
	@Callable
	String classCompareAndSetFail( final String target ){
		field_class.compareAndSet(target, target);
		return field_class.get();
	}
	@Callable
	String classGetAndSet( final String target ){
		return field_class.getAndSet( target );
	}
	@SuppressWarnings("unlikely-arg-type")
	@Callable
	boolean classEquals( final String target ){
		return field_class.equals( field_class ) && field_class.equals( field_class.get() ) && field_class.equals( target );
	}
	@Callable
	boolean classCompareTo(){
		return true;
	}
	@Callable
	boolean classToString( final String value ){
		return Objects.toString(value).equals(field_class.toString());
	}
	@Callable
	boolean classTryLock(){
		if( field_class.tryLock() ) {
			try {
				return true;
			} finally {
				field_class.unlock();
			}
		}
		return false;
	}
	@Callable
	boolean classTryLockTimeout(){
		if( field_class.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_class.unlock();
			}
		}
		return false;
	}

	// NON-NULL
	@Autowired @NonNull
	SClass<String> field_class_nonnull;
	@Callable
	String classNonnullSet( final String value ){
		return field_class_nonnull.set( value );
	}
	@Callable
	boolean classNonnullSetNull(){
		try {
			field_class_nonnull.set( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean classNonnullCompareAndSet( final String expected, final String update ){
		return field_class_nonnull.compareAndSet( expected, update );
	}
	@Callable
	boolean classNonnullCompareAndSetNullExpected( final String expected ){
		try {
			field_class_nonnull.compareAndSet( expected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean classNonnullCompareAndSetNullUnexpected( final String unexpected ){
		try {
			field_class_nonnull.compareAndSet( unexpected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	String classNonnullGetAndSet( final String value ){
		return field_class_nonnull.getAndSet( value );
	}
	@Callable
	boolean classNonnullGetAndSetNull(){
		try {
			field_class_nonnull.getAndSet( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}

	// UNINITIALIZED
	@Autowired @Uninitialized
	SClass<String> field_class_uninitialized;
	@Callable
	boolean classUninitializedIsInitializedBefore(){
		return (
			field_class_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean classUninitializedGet(){
		return (
			field_class_uninitialized.get() == null &&
			field_class_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean classUninitializedGetValue(){
		return (
			field_class_uninitialized.getValue() == null &&
			field_class_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean classUninitializedLock(){
		boolean result = false;
		try( Unlocker unlocker = field_class_uninitialized.lock() ){
			result = field_class_uninitialized.isLocked();
		}
		return result == true && field_class_uninitialized.isInitialized() == false;
	}
	@Callable
	void classUninitializedInitialize( final String value ){
		field_class_uninitialized.set( value );
	}
	@Callable
	boolean classUninitializedIsInitializedAfter(){
		return (
			field_class_uninitialized.isInitialized() == true
		);
	}
	@Callable
	boolean classUninitializedInitializeAfter(){
		field_class_uninitialized.initialize();
		return field_class_uninitialized.isInitialized();
	}

	// UNINITIALIZED 2
	@Autowired @Uninitialized
	SString field_class_uninitialized2;
	@Callable
	boolean classUninitialized2Initialize(){
		field_class_uninitialized2.initialize();
		return field_class_uninitialized2.isInitialized();
	}

	//---------------------------------------------
	// DOUBLE
	//---------------------------------------------
	@Autowired
	SDouble field_double;
	@Callable
	double doubleGet(){
		return field_double.get();
	}
	@Callable
	double doubleSet(){
		field_double.set( 0.2 );
		return field_double.get();
	}
	@Callable
	double doubleCompareAndSet( final double target ){
		field_double.compareAndSet(field_double.get(), target);
		return field_double.get();
	}
	@Callable
	double doubleCompareAndSetFail( final double target ){
		field_double.compareAndSet(target, target);
		return field_double.get();
	}
	@Callable
	double doubleGetAndSet( final double target ){
		return field_double.getAndSet( target );
	}
	@Callable
	boolean doubleEquals( final double target ){
		return field_double.equals( field_double ) && field_double.equals( field_double.get() ) &&
			field_double.equals( (Number) field_double.get() ) && field_double.equals( target );
	}
	@Callable
	boolean doubleCompareTo(){
		return field_double.compareTo( field_double.get() ) == 0 && 0 < field_double.compareTo( 0.0 ) && field_double.compareTo( 0.3 ) < 0;
	}
	@Callable
	boolean doubleToString( final double value ){
		return Objects.toString(value).equals(field_double.toString());
	}
	@Callable
	boolean doubleTryLock(){
		if( field_double.tryLock() ) {
			try {
				return true;
			} finally {
				field_double.unlock();
			}
		}
		return false;
	}
	@Callable
	boolean doubleTryLockTimeout(){
		if( field_double.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_double.unlock();
			}
		}
		return false;
	}

	// NON-NULL
	@Autowired @NonNull
	SDouble field_double_nonnull;
	@Callable
	Double doubleNonnullSet( final Double value ){
		return field_double_nonnull.set( value );
	}
	@Callable
	boolean doubleNonnullSetNull(){
		try {
			field_double_nonnull.set( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean doubleNonnullCompareAndSet( final Double expected, final Double update ){
		return field_double_nonnull.compareAndSet( expected, update );
	}
	@Callable
	boolean doubleNonnullCompareAndSetNullExpected( final Double expected ){
		try {
			field_double_nonnull.compareAndSet( expected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean doubleNonnullCompareAndSetNullUnexpected( final Double unexpected ){
		try {
			field_double_nonnull.compareAndSet( unexpected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	Double doubleNonnullGetAndSet( final Double value ){
		return field_double_nonnull.getAndSet( value );
	}
	@Callable
	boolean doubleNonnullGetAndSetNull(){
		try {
			field_double_nonnull.getAndSet( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}

	// UNINITIALIZED
	@Autowired @Uninitialized
	SDouble field_double_uninitialized;
	@Callable
	boolean doubleUninitializedIsInitializedBefore(){
		return (
			field_double_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean doubleUninitializedGet(){
		return (
			field_double_uninitialized.get() == null &&
			field_double_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean doubleUninitializedGetValue(){
		return (
			field_double_uninitialized.getValue() == null &&
			field_double_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean doubleUninitializedLock(){
		boolean result = false;
		try( Unlocker unlocker = field_double_uninitialized.lock() ){
			result = field_double_uninitialized.isLocked();
		}
		return result == true && field_double_uninitialized.isInitialized() == false;
	}
	@Callable
	void doubleUninitializedInitialize( final Double value ){
		field_double_uninitialized.set( value );
	}
	@Callable
	boolean doubleUninitializedIsInitializedAfter(){
		return (
			field_double_uninitialized.isInitialized() == true
		);
	}
	@Callable
	boolean doubleUninitializedInitializeAfter(){
		field_double_uninitialized.initialize();
		return field_double_uninitialized.isInitialized();
	}

	// UNINITIALIZED 2
	@Autowired @Uninitialized
	SString field_double_uninitialized2;
	@Callable
	boolean doubleUninitialized2Initialize(){
		field_double_uninitialized2.initialize();
		return field_double_uninitialized2.isInitialized();
	}

	//---------------------------------------------
	// FLOAT
	//---------------------------------------------
	@Autowired
	SFloat field_float;
	@Callable
	float floatGet(){
		return field_float.get();
	}
	@Callable
	float floatSet(){
		field_float.set( 0.2F );
		return field_float.get();
	}
	@Callable
	float floatCompareAndSet( final float target ){
		field_float.compareAndSet(field_float.get(), target);
		return field_float.get();
	}
	@Callable
	float floatCompareAndSetFail( final float target ){
		field_float.compareAndSet(target, target);
		return field_float.get();
	}
	@Callable
	float floatGetAndSet( final float target ){
		return field_float.getAndSet( target );
	}
	@Callable
	boolean floatEquals( final float target ){
		return field_float.equals( field_float ) && field_float.equals( field_float.get() ) &&
			field_float.equals( (Number)field_float.get() ) && field_float.equals( target );
	}
	@Callable
	boolean floatCompareTo(){
		return field_float.compareTo( field_float.get() ) == 0 && 0 < field_float.compareTo( 0.0F ) && field_float.compareTo( 0.3F ) < 0;
	}
	@Callable
	boolean floatToString( final float value ){
		return Objects.toString(value).equals(field_float.toString());
	}
	@Callable
	boolean floatTryLock(){
		if( field_float.tryLock() ) {
			try {
				return true;
			} finally {
				field_float.unlock();
			}
		}
		return false;
	}
	@Callable
	boolean floatTryLockTimeout(){
		if( field_float.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_float.unlock();
			}
		}
		return false;
	}

	// NON-NULL
	@Autowired @NonNull
	SFloat field_float_nonnull;
	@Callable
	Float floatNonnullSet( final Float value ){
		return field_float_nonnull.set( value );
	}
	@Callable
	boolean floatNonnullSetNull(){
		try {
			field_float_nonnull.set( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean floatNonnullCompareAndSet( final Float expected, final Float update ){
		return field_float_nonnull.compareAndSet( expected, update );
	}
	@Callable
	boolean floatNonnullCompareAndSetNullExpected( final Float expected ){
		try {
			field_float_nonnull.compareAndSet( expected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean floatNonnullCompareAndSetNullUnexpected( final Float unexpected ){
		try {
			field_float_nonnull.compareAndSet( unexpected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	Float floatNonnullGetAndSet( final Float value ){
		return field_float_nonnull.getAndSet( value );
	}
	@Callable
	boolean floatNonnullGetAndSetNull(){
		try {
			field_float_nonnull.getAndSet( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}

	// UNINITIALIZED
	@Autowired @Uninitialized
	SFloat field_float_uninitialized;
	@Callable
	boolean floatUninitializedIsInitializedBefore(){
		return (
			field_float_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean floatUninitializedGet(){
		return (
			field_float_uninitialized.get() == null &&
			field_float_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean floatUninitializedGetValue(){
		return (
			field_float_uninitialized.getValue() == null &&
			field_float_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean floatUninitializedLock(){
		boolean result = false;
		try( Unlocker unlocker = field_float_uninitialized.lock() ){
			result = field_float_uninitialized.isLocked();
		}
		return result == true && field_float_uninitialized.isInitialized() == false;
	}
	@Callable
	void floatUninitializedInitialize( final Float value ){
		field_float_uninitialized.set( value );
	}
	@Callable
	boolean floatUninitializedIsInitializedAfter(){
		return (
			field_float_uninitialized.isInitialized() == true
		);
	}
	@Callable
	boolean floatUninitializedInitializeAfter(){
		field_float_uninitialized.initialize();
		return field_float_uninitialized.isInitialized();
	}

	// UNINITIALIZED 2
	@Autowired @Uninitialized
	SString field_float_uninitialized2;
	@Callable
	boolean floatUninitialized2Initialize(){
		field_float_uninitialized2.initialize();
		return field_float_uninitialized2.isInitialized();
	}

	//---------------------------------------------
	// INTEGER
	//---------------------------------------------
	@Autowired
	SInteger field_int;
	@Callable
	int intGet(){
		return field_int.get();
	}
	@Callable
	int intSet(){
		field_int.set( 2 );
		return field_int.get();
	}
	@Callable
	int intCompareAndSet( final int target ){
		field_int.compareAndSet(field_int.get(), target);
		return field_int.get();
	}
	@Callable
	int intCompareAndSetFail( final int target ){
		field_int.compareAndSet(target, target);
		return field_int.get();
	}
	@Callable
	int intGetAndSet( final int target ){
		return field_int.getAndSet( target );
	}
	@Callable
	boolean intEquals( final int target ){
		return field_int.equals( field_int ) && field_int.equals( field_int.get() ) &&
			field_int.equals( (Number) field_int.get() ) && field_int.equals( target );
	}
	@Callable
	boolean intCompareTo(){
		return field_int.compareTo( field_int.get() ) == 0 && 0 < field_int.compareTo( 0 ) && field_int.compareTo( 3 ) < 0;
	}
	@Callable
	boolean intToString( final int value ){
		return Objects.toString(value).equals(field_int.toString());
	}
	@Callable
	boolean intTryLock(){
		if( field_int.tryLock() ) {
			try {
				return true;
			} finally {
				field_int.unlock();
			}
		}
		return false;
	}
	@Callable
	boolean intTryLockTimeout(){
		if( field_int.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_int.unlock();
			}
		}
		return false;
	}

	// NON-NULL
	@Autowired @NonNull
	SInteger field_int_nonnull;
	@Callable
	Integer intNonnullSet( final Integer value ){
		return field_int_nonnull.set( value );
	}
	@Callable
	boolean intNonnullSetNull(){
		try {
			field_int_nonnull.set( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean intNonnullCompareAndSet( final Integer expected, final Integer update ){
		return field_int_nonnull.compareAndSet( expected, update );
	}
	@Callable
	boolean intNonnullCompareAndSetNullExpected( final Integer expected ){
		try {
			field_int_nonnull.compareAndSet( expected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean intNonnullCompareAndSetNullUnexpected( final Integer unexpected ){
		try {
			field_int_nonnull.compareAndSet( unexpected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	Integer intNonnullGetAndSet( final Integer value ){
		return field_int_nonnull.getAndSet( value );
	}
	@Callable
	boolean intNonnullGetAndSetNull(){
		try {
			field_int_nonnull.getAndSet( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}

	// UNINITIALIZED
	@Autowired @Uninitialized
	SInteger field_integer_uninitialized;
	@Callable
	boolean intUninitializedIsInitializedBefore(){
		return (
			field_integer_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean intUninitializedGet(){
		return (
			field_integer_uninitialized.get() == null &&
			field_integer_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean intUninitializedGetValue(){
		return (
			field_integer_uninitialized.getValue() == null &&
			field_integer_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean intUninitializedLock(){
		boolean result = false;
		try( Unlocker unlocker = field_integer_uninitialized.lock() ){
			result = field_integer_uninitialized.isLocked();
		}
		return result == true && field_integer_uninitialized.isInitialized() == false;
	}
	@Callable
	void intUninitializedInitialize( final Integer value ){
		field_integer_uninitialized.set( value );
	}
	@Callable
	boolean intUninitializedIsInitializedAfter(){
		return (
			field_integer_uninitialized.isInitialized() == true
		);
	}
	@Callable
	boolean intUninitializedInitializeAfter(){
		field_integer_uninitialized.initialize();
		return field_integer_uninitialized.isInitialized();
	}

	// UNINITIALIZED 2
	@Autowired @Uninitialized
	SString field_integer_uninitialized2;
	@Callable
	boolean integerUninitialized2Initialize(){
		field_integer_uninitialized2.initialize();
		return field_integer_uninitialized2.isInitialized();
	}

	//---------------------------------------------
	// LONG
	//---------------------------------------------
	@Autowired
	SLong field_long;
	@Callable
	long longGet(){
		return field_long.get();
	}
	@Callable
	long longSet(){
		field_long.set( 2L );
		return field_long.get();
	}
	@Callable
	long longCompareAndSet( final long target ){
		field_long.compareAndSet(field_long.get(), target);
		return field_long.get();
	}
	@Callable
	long longCompareAndSetFail( final long target ){
		field_long.compareAndSet(target, target);
		return field_long.get();
	}
	@Callable
	long longGetAndSet( final long target ){
		return field_long.getAndSet( target );
	}
	@Callable
	boolean longEquals( final long target ){
		return field_long.equals( field_long ) && field_long.equals( field_long.get() ) &&
			field_long.equals( (Number) field_long.get() ) && field_long.equals( target );
	}
	@Callable
	boolean longCompareTo(){
		return field_long.compareTo( field_long.get() ) == 0 && 0 < field_long.compareTo( 0L ) && field_long.compareTo( 3L ) < 0;
	}
	@Callable
	boolean longToString( final long value ){
		return Objects.toString(value).equals(field_long.toString());
	}
	@Callable
	boolean longTryLock(){
		if( field_long.tryLock() ) {
			try {
				return true;
			} finally {
				field_long.unlock();
			}
		}
		return false;
	}
	@Callable
	boolean longTryLockTimeout(){
		if( field_long.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_long.unlock();
			}
		}
		return false;
	}

	// NON-NULL
	@Autowired @NonNull
	SLong field_long_nonnull;
	@Callable
	Long longNonnullSet( final Long value ){
		return field_long_nonnull.set( value );
	}
	@Callable
	boolean longNonnullSetNull(){
		try {
			field_long_nonnull.set( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean longNonnullCompareAndSet( final Long expected, final Long update ){
		return field_long_nonnull.compareAndSet( expected, update );
	}
	@Callable
	boolean longNonnullCompareAndSetNullExpected( final Long expected ){
		try {
			field_long_nonnull.compareAndSet( expected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean longNonnullCompareAndSetNullUnexpected( final Long unexpected ){
		try {
			field_long_nonnull.compareAndSet( unexpected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	Long longNonnullGetAndSet( final Long value ){
		return field_long_nonnull.getAndSet( value );
	}
	@Callable
	boolean longNonnullGetAndSetNull(){
		try {
			field_long_nonnull.getAndSet( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}

	// UNINITIALIZED
	@Autowired @Uninitialized
	SLong field_long_uninitialized;
	@Callable
	boolean longUninitializedIsInitializedBefore(){
		return (
			field_long_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean longUninitializedGet(){
		return (
			field_long_uninitialized.get() == null &&
			field_long_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean longUninitializedGetValue(){
		return (
			field_long_uninitialized.getValue() == null &&
			field_long_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean longUninitializedLock(){
		boolean result = false;
		try( Unlocker unlocker = field_long_uninitialized.lock() ){
			result = field_long_uninitialized.isLocked();
		}
		return result == true && field_long_uninitialized.isInitialized() == false;
	}
	@Callable
	void longUninitializedInitialize( final Long value ){
		field_long_uninitialized.set( value );
	}
	@Callable
	boolean longUninitializedIsInitializedAfter(){
		return (
			field_long_uninitialized.isInitialized() == true
		);
	}
	@Callable
	boolean longUninitializedInitializeAfter(){
		field_long_uninitialized.initialize();
		return field_long_uninitialized.isInitialized();
	}

	// UNINITIALIZED 2
	@Autowired @Uninitialized
	SString field_long_uninitialized2;
	@Callable
	boolean longUninitialized2Initialize(){
		field_long_uninitialized2.initialize();
		return field_long_uninitialized2.isInitialized();
	}

	//---------------------------------------------
	// JSON NODE
	//---------------------------------------------
	@Autowired
	SJsonNode field_json_node;
	@Callable
	JsonNode jsonNodeGet(){
		return field_json_node.get();
	}
	@Callable
	JsonNode jsonNodeSet(){
		field_json_node.createArrayNode().add( 128 );
		return field_json_node.get();
	}
	@Callable
	JsonNode jsonNodeCompareAndSet( final JsonNode target ){
		field_json_node.compareAndSet(field_json_node.get(), target);
		return field_json_node.get();
	}
	@Callable
	JsonNode jsonNodeCompareAndSetFail( final JsonNode target ){
		field_json_node.compareAndSet(target, target);
		return field_json_node.get();
	}
	@Callable
	JsonNode jsonNodeGetAndSet( final JsonNode target ){
		return field_json_node.getAndSet( target );
	}
	@Callable
	boolean jsonNodeEquals( final JsonNode target ){
		return field_json_node.equals( field_json_node ) && field_json_node.equals( field_json_node.get() ) && field_json_node.equals( target );
	}
	@Callable
	boolean jsonNodeCompareTo(){
		return true;
	}
	@Callable
	boolean jsonNodeToString( final JsonNode value ){
		return Objects.toString(value).equals(field_json_node.toString());
	}
	@Callable
	boolean jsonNodeTryLock(){
		if( field_json_node.tryLock() ) {
			try {
				return true;
			} finally {
				field_json_node.unlock();
			}
		}
		return false;
	}
	@Callable
	boolean jsonNodeTryLockTimeout(){
		if( field_json_node.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_json_node.unlock();
			}
		}
		return false;
	}

	// NON-NULL
	@Autowired @NonNull
	SJsonNode field_json_node_nonnull;
	@Callable
	JsonNode jsonNodeNonnullSet( final JsonNode value ){
		return field_json_node_nonnull.set( value );
	}
	@Callable
	boolean jsonNodeNonnullSetNull(){
		try {
			field_json_node_nonnull.set( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean jsonNodeNonnullCompareAndSet( final JsonNode expected, final JsonNode update ){
		return field_json_node_nonnull.compareAndSet( expected, update );
	}
	@Callable
	boolean jsonNodeNonnullCompareAndSetNullExpected( final JsonNode expected ){
		try {
			field_json_node_nonnull.compareAndSet( expected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean jsonNodeNonnullCompareAndSetNullUnexpected( final JsonNode unexpected ){
		try {
			field_json_node_nonnull.compareAndSet( unexpected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	JsonNode jsonNodeNonnullGetAndSet( final JsonNode value ){
		return field_json_node_nonnull.getAndSet( value );
	}
	@Callable
	boolean jsonNodeNonnullGetAndSetNull(){
		try {
			field_json_node_nonnull.getAndSet( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}

	// UNINITIALIZED
	@Autowired @Uninitialized
	SJsonNode field_json_node_uninitialized;
	@Callable
	boolean jsonNodeUninitializedIsInitializedBefore(){
		return (
			field_json_node_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean jsonNodeUninitializedGet(){
		return (
			field_json_node_uninitialized.get() == null &&
			field_json_node_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean jsonNodeUninitializedGetValue(){
		return (
			field_json_node_uninitialized.getValue() == null &&
			field_json_node_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean jsonNodeUninitializedLock(){
		boolean result = false;
		try( Unlocker unlocker = field_json_node_uninitialized.lock() ){
			result = field_json_node_uninitialized.isLocked();
		}
		return result == true && field_json_node_uninitialized.isInitialized() == false;
	}
	@Callable
	void jsonNodeUninitializedInitialize( final JsonNode value ){
		field_json_node_uninitialized.set( value );
	}
	@Callable
	boolean jsonNodeUninitializedIsInitializedAfter(){
		return (
			field_json_node_uninitialized.isInitialized() == true
		);
	}
	@Callable
	boolean jsonNodeUninitializedInitializeAfter(){
		field_json_node_uninitialized.initialize();
		return field_json_node_uninitialized.isInitialized();
	}

	// UNINITIALIZED 2
	@Autowired @Uninitialized
	SString field_json_node_uninitialized2;
	@Callable
	boolean jsonNodeUninitialized2Initialize(){
		field_json_node_uninitialized2.initialize();
		return field_json_node_uninitialized2.isInitialized();
	}

	// DIRTY
	@Autowired
	SJsonNode field_json_node_dirty;
	@OnCreate
	void initJsonNodeDirty(){
		field_json_node_dirty.createObjectNode().put( "1", 1 );
	}
	@OnChange( "field_json_node_dirty" )
	void onJsonNodeDirtyChange( final JsonNode newNode, final JsonNode oldNode ){
		((ObjectNode)field_json_node_dirty.get()).put( "3", 3 );
		field_json_node_dirty.toDirty();
	}

	//---------------------------------------------
	// OBJECT NODE
	//---------------------------------------------
	@Autowired
	SObjectNode field_object_node;
	@Callable
	ObjectNode objectNodeGet(){
		return field_object_node.get();
	}
	@Callable
	ObjectNode objectNodeSet(){
		field_object_node.create().put( "2", 2 );
		return field_object_node.get();
	}
	@Callable
	ObjectNode objectNodeCompareAndSet( final ObjectNode target ){
		field_object_node.compareAndSet(field_object_node.get(), target);
		return field_object_node.get();
	}
	@Callable
	ObjectNode objectNodeCompareAndSetFail( final ObjectNode target ){
		field_object_node.compareAndSet(target, target);
		return field_object_node.get();
	}
	@Callable
	ObjectNode objectNodeGetAndSet( final ObjectNode target ){
		return field_object_node.getAndSet( target );
	}
	@Callable
	boolean objectNodeEquals( final ObjectNode target ){
		return field_object_node.equals( field_object_node ) && field_object_node.equals( field_object_node.get() ) && field_object_node.equals( target );
	}
	@Callable
	boolean objectNodeCompareTo(){
		return true;
	}
	@Callable
	boolean objectNodeToString( final ObjectNode value ){
		return Objects.toString(value).equals(field_object_node.toString());
	}
	@Callable
	boolean objectNodeTryLock(){
		if( field_object_node.tryLock() ) {
			try {
				return true;
			} finally {
				field_object_node.unlock();
			}
		}
		return false;
	}
	@Callable
	boolean objectNodeTryLockTimeout(){
		if( field_object_node.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_object_node.unlock();
			}
		}
		return false;
	}

	// NON-NULL
	@Autowired @NonNull
	SObjectNode field_object_node_nonnull;
	@Callable
	ObjectNode objectNodeNonnullSet( final ObjectNode value ){
		return field_object_node_nonnull.set( value );
	}
	@Callable
	boolean objectNodeNonnullSetNull(){
		try {
			field_object_node_nonnull.set( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean objectNodeNonnullCompareAndSet( final ObjectNode expected, final ObjectNode update ){
		return field_object_node_nonnull.compareAndSet( expected, update );
	}
	@Callable
	boolean objectNodeNonnullCompareAndSetNullExpected( final ObjectNode expected ){
		try {
			field_object_node_nonnull.compareAndSet( expected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean objectNodeNonnullCompareAndSetNullUnexpected( final ObjectNode unexpected ){
		try {
			field_object_node_nonnull.compareAndSet( unexpected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	ObjectNode objectNodeNonnullGetAndSet( final ObjectNode value ){
		return field_object_node_nonnull.getAndSet( value );
	}
	@Callable
	boolean objectNodeNonnullGetAndSetNull(){
		try {
			field_object_node_nonnull.getAndSet( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}

	// UNINITIALIZED
	@Autowired @Uninitialized
	SObjectNode field_object_node_uninitialized;
	@Callable
	boolean objectNodeUninitializedIsInitializedBefore(){
		return (
			field_object_node_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean objectNodeUninitializedGet(){
		return (
			field_object_node_uninitialized.get() == null &&
			field_object_node_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean objectNodeUninitializedGetValue(){
		return (
			field_object_node_uninitialized.getValue() == null &&
			field_object_node_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean objectNodeUninitializedLock(){
		boolean result = false;
		try( Unlocker unlocker = field_object_node_uninitialized.lock() ){
			result = field_object_node_uninitialized.isLocked();
		}
		return result == true && field_object_node_uninitialized.isInitialized() == false;
	}
	@Callable
	void objectNodeUninitializedInitialize( final ObjectNode value ){
		field_object_node_uninitialized.set( value );
	}
	@Callable
	boolean objectNodeUninitializedIsInitializedAfter(){
		return (
			field_object_node_uninitialized.isInitialized() == true
		);
	}
	@Callable
	boolean objectNodeUninitializedInitializeAfter(){
		field_object_node_uninitialized.initialize();
		return field_object_node_uninitialized.isInitialized();
	}

	// UNINITIALIZED 2
	@Autowired @Uninitialized
	SObjectNode field_object_node_uninitialized2;
	@Callable
	boolean objectNodeUninitialized2Initialize(){
		field_object_node_uninitialized2.initialize();
		return field_object_node_uninitialized2.isInitialized();
	}

	// DIRTY
	@Autowired
	SObjectNode field_object_node_dirty;
	@OnCreate
	void initObjectNodeDirty(){
		field_object_node_dirty.create().put( "1", 1 );
	}
	@OnChange( "field_object_node_dirty" )
	void onObjectNodeDirtyChange( final ObjectNode newNode, final ObjectNode oldNode ){
		field_object_node_dirty.get().put( "3", 3 );
		field_object_node_dirty.toDirty();
	}

	//---------------------------------------------
	// STRING
	//---------------------------------------------
	@Autowired
	SString field_string;
	@Callable
	String stringGet(){
		return field_string.get();
	}
	@Callable
	String stringSet(){
		field_string.set( "b" );
		return field_string.get();
	}
	@Callable
	String stringCompareAndSet( final String target ){
		field_string.compareAndSet(field_string.get(), target);
		return field_string.get();
	}
	@Callable
	String stringCompareAndSetFail( final String target ){
		field_string.compareAndSet(target, target);
		return field_string.get();
	}
	@Callable
	String stringGetAndSet( final String target ){
		return field_string.getAndSet( target );
	}
	@Callable
	boolean stringEquals( final String target ){
		return field_string.equals( field_string ) && field_string.equals( field_string.get() ) && field_string.equals( target );
	}
	@Callable
	boolean stringCompareTo(){
		return field_string.compareTo( field_string.get() ) == 0 && 0 < field_string.compareTo( "`" ) && field_string.compareTo( "c" ) < 0;
	}
	@Callable
	boolean stringToString( final String value ){
		return Objects.toString(value).equals(field_string.toString());
	}
	@Callable
	boolean stringTryLock(){
		if( field_string.tryLock() ) {
			try {
				return true;
			} finally {
				field_string.unlock();
			}
		}
		return false;
	}
	@Callable
	boolean stringTryLockTimeout(){
		if( field_string.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_string.unlock();
			}
		}
		return false;
	}

	// NON-NULL
	@Autowired @NonNull
	SString field_string_nonnull;
	@Callable
	String stringNonnullSet( final String value ){
		return field_string_nonnull.set( value );
	}
	@Callable
	boolean stringNonnullSetNull(){
		try {
			field_string_nonnull.set( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean stringNonnullCompareAndSet( final String expected, final String update ){
		return field_string_nonnull.compareAndSet( expected, update );
	}
	@Callable
	boolean stringNonnullCompareAndSetNullExpected( final String expected ){
		try {
			field_string_nonnull.compareAndSet( expected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	boolean stringNonnullCompareAndSetNullUnexpected( final String unexpected ){
		try {
			field_string_nonnull.compareAndSet( unexpected, null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}
	@Callable
	String stringNonnullGetAndSet( final String value ){
		return field_string_nonnull.getAndSet( value );
	}
	@Callable
	boolean stringNonnullGetAndSetNull(){
		try {
			field_array_node_nonnull.getAndSet( null );
			return false;
		} catch( NullPointerException e ){
			return true;
		}
	}

	// UNINITIALIZED
	@Autowired @Uninitialized
	SString field_string_uninitialized;
	@Callable
	boolean stringUninitializedIsInitializedBefore(){
		return (
			field_string_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean stringUninitializedGet(){
		return (
			field_string_uninitialized.get() == null &&
			field_string_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean stringUninitializedGetValue(){
		return (
			field_string_uninitialized.getValue() == null &&
			field_string_uninitialized.isInitialized() == false
		);
	}
	@Callable
	boolean stringUninitializedLock(){
		boolean result = false;
		try( Unlocker unlocker = field_string_uninitialized.lock() ){
			result = field_string_uninitialized.isLocked();
		}
		return result == true && field_string_uninitialized.isInitialized() == false;
	}
	@Callable
	void stringUninitializedInitialize( final String value ){
		field_string_uninitialized.set( value );
	}
	@Callable
	boolean stringUninitializedIsInitializedAfter(){
		return (
			field_string_uninitialized.isInitialized() == true
		);
	}
	@Callable
	boolean stringUninitializedInitializeAfter(){
		field_string_uninitialized.initialize();
		return field_string_uninitialized.isInitialized();
	}

	// UNINITIALIZED 2
	@Autowired @Uninitialized
	SString field_string_uninitialized2;
	@Callable
	boolean stringUninitialized2Initialize(){
		field_string_uninitialized2.initialize();
		return field_string_uninitialized2.isInitialized();
	}

	// STRING METHODS
	@Autowired
	SString field_string_methods;

	@Autowired
	SString field_string_client_side_methods;

	@Callable
	boolean string_substring_1_check(){
		field_string_methods.set( "abc" );
		return field_string_methods.substring( 1 ).equals( "bc" );
	}
	@Callable
	boolean string_substring_1_null_check(){
		field_string_methods.set( null );
		try {
			field_string_methods.substring( 1 );
			return false;
		} catch( final IndexOutOfBoundsException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}
	@Callable
	boolean string_substring_2_check(){
		field_string_methods.set( "abc" );
		return field_string_methods.substring( 1, 2 ).equals( "b" );
	}
	@Callable
	boolean string_substring_2_null_check(){
		field_string_methods.set( null );
		try {
			field_string_methods.substring( 1, 2 );
			return false;
		} catch( final IndexOutOfBoundsException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}
	@Callable
	boolean string_trim_check(){
		field_string_methods.set( " abc " );
		return field_string_methods.trim().equals( "abc" );
	}
	@Callable
	boolean string_trim_null_check(){
		field_string_methods.set( null );
		return field_string_methods.trim() == null;
	}
	@Callable
	boolean string_toUpperCase_check(){
		field_string_methods.set( "abc" );
		return field_string_methods.toUpperCase().equals( "ABC" );
	}
	@Callable
	boolean string_toUpperCase_null_check(){
		field_string_methods.set( null );
		return field_string_methods.toUpperCase() == null;
	}
	@Callable
	boolean string_toLowerCase_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.toLowerCase().equals( "abc" );
	}
	@Callable
	boolean string_toLowerCase_null_check(){
		field_string_methods.set( null );
		return field_string_methods.toLowerCase() == null;
	}
	@Callable
	boolean string_length_check(){
		field_string_methods.set( "abc" );
		return field_string_methods.length() == 3;
	}
	@Callable
	boolean string_length_null_check(){
		field_string_methods.set( null );
		return field_string_methods.length() < 0;
	}
	@Callable
	boolean string_isEmpty_check(){
		field_string_methods.set( "abc" );
		return !field_string_methods.isEmpty();
	}
	@Callable
	boolean string_isEmpty_null_check(){
		field_string_methods.set( null );
		return field_string_methods.isEmpty();
	}
	@Callable
	boolean string_compareTo_check(){
		field_string_methods.set( "abc" );
		return field_string_methods.compareTo( "abc" ) == 0 && field_string_methods.compareTo( null ) > 0 &&
				field_string_methods.compareTo( "abcd" ) < 0 && field_string_methods.compareTo( "ab" ) > 0;
	}
	@Callable
	boolean string_compareTo_null_check(){
		field_string_methods.set( null );
		return field_string_methods.compareTo( "abc" ) < 0 && field_string_methods.compareTo( null ) == 0;
	}
	@Callable
	boolean string_compareToIgnoreCase_check(){
		field_string_methods.set( "abc" );
		return field_string_methods.compareToIgnoreCase( "aBc" ) == 0 && field_string_methods.compareToIgnoreCase( null ) > 0 &&
				field_string_methods.compareToIgnoreCase( "aBcd" ) < 0 && field_string_methods.compareToIgnoreCase( "aB" ) > 0;
	}
	@Callable
	boolean string_compareToIgnoreCase_null_check(){
		field_string_methods.set( null );
		return field_string_methods.compareToIgnoreCase( "abc" ) < 0 && field_string_methods.compareToIgnoreCase( null ) == 0;
	}
	@Callable
	boolean string_toUpperCase_locale_check(){
		field_string_methods.set( "abc" );
		return field_string_methods.toUpperCase( Locale.ENGLISH ).equals( "ABC" );
	}
	@Callable
	boolean string_toUpperCase_locale_null_check(){
		field_string_methods.set( null );
		return field_string_methods.toUpperCase( Locale.ENGLISH ) == null;
	}
	@Callable
	boolean string_toLowerCase_locale_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.toLowerCase( Locale.ENGLISH ).equals( "abc" );
	}
	@Callable
	boolean string_toLowerCase_locale_null_check(){
		field_string_methods.set( null );
		return field_string_methods.toLowerCase( Locale.ENGLISH ) == null;
	}
	@Callable
	boolean string_startsWith_1_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.startsWith( "A" ) && !field_string_methods.startsWith( "B" );
	}
	@Callable
	boolean string_startsWith_1_null_check(){
		field_string_methods.set( null );
		return !field_string_methods.startsWith( "A" );
	}
	@Callable
	boolean string_startsWith_2_check(){
		field_string_methods.set( "ABC" );
		return !field_string_methods.startsWith( "A", 1 ) && field_string_methods.startsWith( "B", 1 );
	}
	@Callable
	boolean string_startsWith_2_null_check(){
		field_string_methods.set( null );
		return !field_string_methods.startsWith( "A", 1 );
	}
	@Callable
	boolean string_matches_check(){
		field_string_methods.set( "ABC" );
		return !field_string_methods.matches("[a-z]+") && field_string_methods.matches("[A-Z]+");
	}
	@Callable
	boolean string_matches_null_check(){
		field_string_methods.set( null );
		return !field_string_methods.matches("[a-z]+") && !field_string_methods.matches("[A-Z]+");
	}
	@Callable
	boolean string_lastIndexOf_1_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.lastIndexOf('B') == 1 && field_string_methods.lastIndexOf('Z') < 0;
	}
	@Callable
	boolean string_lastIndexOf_1_null_check(){
		field_string_methods.set( null );
		return field_string_methods.lastIndexOf('B') < 0 && field_string_methods.lastIndexOf('Z') < 0;
	}
	@Callable
	boolean string_lastIndexOf_2_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.lastIndexOf("B") == 1 && field_string_methods.lastIndexOf("Z") < 0;
	}
	@Callable
	boolean string_lastIndexOf_2_null_check(){
		field_string_methods.set( null );
		return field_string_methods.lastIndexOf("B") < 0 && field_string_methods.lastIndexOf("Z") < 0;
	}
	@Callable
	boolean string_lastIndexOf_3_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.lastIndexOf('B', 1) == 1 && field_string_methods.lastIndexOf('Z', 1) < 0;
	}
	@Callable
	boolean string_lastIndexOf_3_null_check(){
		field_string_methods.set( null );
		return field_string_methods.lastIndexOf('B', 1) < 0 && field_string_methods.lastIndexOf('Z', 1) < 0;
	}
	@Callable
	boolean string_lastIndexOf_4_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.lastIndexOf("B", 1) == 1 && field_string_methods.lastIndexOf("Z", 1) < 0;
	}
	@Callable
	boolean string_lastIndexOf_4_null_check(){
		field_string_methods.set( null );
		return field_string_methods.lastIndexOf("B", 1) < 0 && field_string_methods.lastIndexOf("Z", 1) < 0;
	}
	@Callable
	boolean string_indexOf_1_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.indexOf('B') == 1 && field_string_methods.indexOf('Z') < 0;
	}
	@Callable
	boolean string_indexOf_1_null_check(){
		field_string_methods.set( null );
		return field_string_methods.indexOf('B') < 0 && field_string_methods.indexOf('Z') < 0;
	}
	@Callable
	boolean string_indexOf_2_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.indexOf("B") == 1 && field_string_methods.indexOf("Z") < 0;
	}
	@Callable
	boolean string_indexOf_2_null_check(){
		field_string_methods.set( null );
		return field_string_methods.indexOf("B") < 0 && field_string_methods.indexOf("Z") < 0;
	}
	@Callable
	boolean string_indexOf_3_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.indexOf('B',1) == 1 && field_string_methods.indexOf('Z',1) < 0;
	}
	@Callable
	boolean string_indexOf_3_null_check(){
		field_string_methods.set( null );
		return field_string_methods.indexOf('B',1) < 0 && field_string_methods.indexOf('Z',1) < 0;
	}
	@Callable
	boolean string_indexOf_4_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.indexOf("B",1) == 1 && field_string_methods.indexOf("Z",1) < 0;
	}
	@Callable
	boolean string_indexOf_4_null_check(){
		field_string_methods.set( null );
		return field_string_methods.indexOf("B",1) < 0 && field_string_methods.indexOf("Z",1) < 0;
	}
	@Callable
	boolean string_equalsIgnoreCase_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.equalsIgnoreCase("aBc") && !field_string_methods.equalsIgnoreCase("z");
	}
	@Callable
	boolean string_equalsIgnoreCase_null_check(){
		field_string_methods.set( null );
		return !field_string_methods.equalsIgnoreCase("B") && field_string_methods.equalsIgnoreCase(null);
	}
	@Callable
	boolean string_endsWith_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.endsWith("C") && !field_string_methods.endsWith("D");
	}
	@Callable
	boolean string_endsWith_null_check(){
		field_string_methods.set( null );
		return !field_string_methods.endsWith("B") && !field_string_methods.endsWith(null);
	}
	@Callable
	boolean string_contains_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.contains("C") && !field_string_methods.contains("D");
	}
	@Callable
	boolean string_contains_null_check(){
		field_string_methods.set( null );
		return !field_string_methods.contains("B") && !field_string_methods.contains(null);
	}
	@Callable
	boolean string_concat_check(){
		field_string_methods.set( "ABC" );
		return field_string_methods.concat("D").equals("ABCD") && field_string_methods.concat(null).equals("ABC");
	}
	@Callable
	boolean string_concat_null_check(){
		field_string_methods.set( null );
		return field_string_methods.concat("B") == null && field_string_methods.concat(null) == null;
	}

	//---------------------------------------------
	// COMPONENT FACTORY
	//---------------------------------------------
	@Autowired
	ComponentFactory<BasicsComponent> field_component_factory;

	//---------------------------------------------
	// LOCALE
	//---------------------------------------------
	@Autowired
	SString field_locale_country;

	@Autowired
	SString field_locale_language;

	@OnCreate
	void init(){
		final Locale locale = getLocale();
		final List<Locale> locales = getLocales();
		if( 0 < locales.size() ) {
			final Locale first = locales.get( 0 );
			if( locale.equals( first ) ) {
				field_locale_country.set( locale.getCountry() );
				field_locale_language.set( locale.getLanguage() );
			}
		}
	}

	//---------------------------------------------
	// UPDATE
	//---------------------------------------------
	@Autowired
	SString field_for_update;

	@OnChange( "field_for_update" )
	void onChangeString( final String value ){
		field_for_update.set( value + value );
	}

	@Callable
	void startUpdate(){
		field_for_update.set("A");
	}

	//---------------------------------------------
	// ReadOnly Uninitialized NonNull
	//---------------------------------------------
	@Autowired @ReadOnly @Uninitialized @NonNull
	SString field_combined;

	@Callable
	boolean combinedReadOnlyCheck(){
		return field_combined.isReadOnly();
	}

	@Callable
	boolean combinedUninitializedCheck(){
		return !field_combined.isInitialized();
	}

	@Callable
	boolean combinedNonNullCheck(){
		return field_combined.isNonNull();
	}

	//---------------------------------------------
	// CONTROLLER METHODS
	//---------------------------------------------
	AtomicBoolean getName_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getParent_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getParentAsFactory_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getParents_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getRemoteAddress_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean show_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean hide_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean isShown_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean isHidden_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean isReadOnly_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean isNonNull_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean isHistorical_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getPrincipal_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getScheduler_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getSessionId_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getSubSessionId_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean lock_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean unlock_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean tryLock_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean tryLock_timeout_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean isLocked_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean isLockedByCurrentThread_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean execute_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean execute_runnable_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean execute_callable_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean timeout_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean timeout_runnable_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean timeout_callable_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean interval_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean interval_parameter_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean interval_runnable_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean interval_runnable_startAt_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean cancel_by_id_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean cancel_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean cancelAll_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean triggerAndWait_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean trigger_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getParameter_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getParameters_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getParameterMap_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getFactoryParameters_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getAttributes_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getLocales_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean getLocale_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean notify_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean notifyAsync_PostConstruct_result = new AtomicBoolean( false );

	@PostConstruct
	void onPostCreate(){
		// getName
		try {
			getName();
			getName_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getName_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getName_PostConstruct_result.set( false );
		}

		// getParent
		try {
			getParent();
			getParent_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getParent_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getParent_PostConstruct_result.set( false );
		}

		// getParentAsFactory
		try {
			getParentAsFactory();
			getParentAsFactory_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getParentAsFactory_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getParentAsFactory_PostConstruct_result.set( false );
		}

		// getParents
		try {
			getParents();
			getParents_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getParents_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getParents_PostConstruct_result.set( false );
		}

		// getRemoteAddress
		try {
			getRemoteAddress();
			getRemoteAddress_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getRemoteAddress_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getRemoteAddress_PostConstruct_result.set( false );
		}

		// show
		try {
			show();
			show_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			show_PostConstruct_result.set( true );
		} catch( final Exception e ){
			show_PostConstruct_result.set( false );
		}

		// hide
		try {
			hide();
			hide_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			hide_PostConstruct_result.set( true );
		} catch( final Exception e ){
			hide_PostConstruct_result.set( false );
		}

		// isShown
		try {
			isShown();
			isShown_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			isShown_PostConstruct_result.set( true );
		} catch( final Exception e ){
			isShown_PostConstruct_result.set( false );
		}

		// isHidden
		try {
			isHidden();
			isHidden_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			isHidden_PostConstruct_result.set( true );
		} catch( final Exception e ){
			isHidden_PostConstruct_result.set( false );
		}

		// isReadOnly
		try {
			isReadOnly();
			isReadOnly_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			isReadOnly_PostConstruct_result.set( true );
		} catch( final Exception e ){
			isReadOnly_PostConstruct_result.set( false );
		}

		// isNonNull
		try {
			isNonNull();
			isNonNull_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			isNonNull_PostConstruct_result.set( true );
		} catch( final Exception e ){
			isNonNull_PostConstruct_result.set( false );
		}

		// isHistorical
		try {
			isHistorical();
			isHistorical_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			isHistorical_PostConstruct_result.set( true );
		} catch( final Exception e ){
			isHistorical_PostConstruct_result.set( false );
		}

		// getPrincipal
		try {
			getPrincipal();
			getPrincipal_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getPrincipal_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getPrincipal_PostConstruct_result.set( false );
		}

		// getScheduler
		try {
			getScheduler();
			getScheduler_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getScheduler_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getScheduler_PostConstruct_result.set( false );
		}

		// getSessionId
		try {
			getSessionId();
			getSessionId_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getSessionId_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getSessionId_PostConstruct_result.set( false );
		}

		// getSubSessionId
		try {
			getSubSessionId();
			getSubSessionId_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getSubSessionId_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getSubSessionId_PostConstruct_result.set( false );
		}

		// lock
		try {
			lock();
			lock_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			lock_PostConstruct_result.set( true );
		} catch( final Exception e ){
			lock_PostConstruct_result.set( false );
		}

		// unlock
		try {
			unlock();
			unlock_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			unlock_PostConstruct_result.set( true );
		} catch( final Exception e ){
			unlock_PostConstruct_result.set( false );
		}

		// tryLock
		try {
			tryLock();
			tryLock_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			tryLock_PostConstruct_result.set( true );
		} catch( final Exception e ){
			tryLock_PostConstruct_result.set( false );
		}

		// tryLock timeout
		try {
			tryLock( 0, TimeUnit.MILLISECONDS );
			tryLock_timeout_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			tryLock_timeout_PostConstruct_result.set( true );
		} catch( final Exception e ){
			tryLock_timeout_PostConstruct_result.set( false );
		}

		// isLocked
		try {
			isLocked();
			isLocked_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			isLocked_PostConstruct_result.set( true );
		} catch( final Exception e ){
			isLocked_PostConstruct_result.set( false );
		}

		// isLockedByCurrentThread
		try {
			isLockedByCurrentThread();
			isLockedByCurrentThread_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			isLockedByCurrentThread_PostConstruct_result.set( true );
		} catch( final Exception e ){
			isLockedByCurrentThread_PostConstruct_result.set( false );
		}

		// execute
		try {
			execute("--");
			execute_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			execute_PostConstruct_result.set( true );
		} catch( final Exception e ){
			execute_PostConstruct_result.set( false );
		}

		// execute runnable
		try {
			execute( (Runnable)null );
			execute_runnable_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			execute_runnable_PostConstruct_result.set( true );
		} catch( final Exception e ){
			execute_runnable_PostConstruct_result.set( false );
		}

		// execute callable
		try {
			execute( (java.util.concurrent.Callable<String>)null );
			execute_callable_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			execute_callable_PostConstruct_result.set( true );
		} catch( final Exception e ){
			execute_callable_PostConstruct_result.set( false );
		}

		// timeout
		try {
			timeout( "--", 0 );
			timeout_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			timeout_PostConstruct_result.set( true );
		} catch( final Exception e ){
			timeout_PostConstruct_result.set( false );
		}

		// timeout runnable
		try {
			timeout( (Runnable)null, 0 );
			timeout_runnable_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			timeout_runnable_PostConstruct_result.set( true );
		} catch( final Exception e ){
			timeout_runnable_PostConstruct_result.set( false );
		}

		// timeout callable
		try {
			timeout( (java.util.concurrent.Callable<String>)null, 0 );
			timeout_callable_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			timeout_callable_PostConstruct_result.set( true );
		} catch( final Exception e ){
			timeout_callable_PostConstruct_result.set( false );
		}

		// interval
		try {
			interval( "--", 0 );
			interval_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			interval_PostConstruct_result.set( true );
		} catch( final Exception e ){
			interval_PostConstruct_result.set( false );
		}

		// interval parameter
		try {
			interval( "--", 0, 0, 0 );
			interval_parameter_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			interval_parameter_PostConstruct_result.set( true );
		} catch( final Exception e ){
			interval_parameter_PostConstruct_result.set( false );
		}

		// interval runnable
		try {
			interval( (Runnable)null, 0 );
			interval_runnable_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			interval_runnable_PostConstruct_result.set( true );
		} catch( final Exception e ){
			interval_runnable_PostConstruct_result.set( false );
		}

		// interval runnable startAt
		try {
			interval( (Runnable)null, 0, 0 );
			interval_runnable_startAt_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			interval_runnable_startAt_PostConstruct_result.set( true );
		} catch( final Exception e ){
			interval_runnable_startAt_PostConstruct_result.set( false );
		}

		// cancel by id
		try {
			cancel( 0 );
			cancel_by_id_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			cancel_by_id_PostConstruct_result.set( true );
		} catch( final Exception e ){
			cancel_by_id_PostConstruct_result.set( false );
		}

		// cancel
		try {
			cancel();
			cancel_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			cancel_PostConstruct_result.set( true );
		} catch( final Exception e ){
			cancel_PostConstruct_result.set( false );
		}

		// cancelAll
		try {
			cancelAll();
			cancelAll_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			cancelAll_PostConstruct_result.set( true );
		} catch( final Exception e ){
			cancelAll_PostConstruct_result.set( false );
		}

		// triggerAndWait
		try {
			triggerAndWait( "--", 0 );
			triggerAndWait_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			triggerAndWait_PostConstruct_result.set( true );
		} catch( final Exception e ){
			triggerAndWait_PostConstruct_result.set( false );
		}

		// trigger
		try {
			trigger( "--" );
			trigger_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			trigger_PostConstruct_result.set( true );
		} catch( final Exception e ){
			trigger_PostConstruct_result.set( false );
		}

		// getParameter
		try {
			getParameter( "--" );
			getParameter_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getParameter_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getParameter_PostConstruct_result.set( false );
		}

		// getParamters
		try {
			getParameters( "--" );
			getParameters_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getParameters_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getParameters_PostConstruct_result.set( false );
		}

		// getParameterMap
		try {
			getParameterMap();
			getParameterMap_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getParameterMap_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getParameterMap_PostConstruct_result.set( false );
		}

		// getFactoryParameters
		try {
			getFactoryParameters();
			getFactoryParameters_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getFactoryParameters_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getFactoryParameters_PostConstruct_result.set( false );
		}

		// getAttributes
		try {
			getAttributes();
			getAttributes_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getAttributes_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getAttributes_PostConstruct_result.set( false );
		}

		// getLocales
		try {
			getLocales();
			getLocales_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getLocales_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getLocales_PostConstruct_result.set( false );
		}

		// getLocale
		try {
			getLocale();
			getLocale_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			getLocale_PostConstruct_result.set( true );
		} catch( final Exception e ){
			getLocale_PostConstruct_result.set( false );
		}

		// notify
		try {
			notify( "--" );
			notify_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			notify_PostConstruct_result.set( true );
		} catch( final Exception e ){
			notify_PostConstruct_result.set( false );
		}

		// notifyAsync
		try {
			notifyAsync( "--" );
			notifyAsync_PostConstruct_result.set( false );
		} catch( final NotReadyException e ){
			notifyAsync_PostConstruct_result.set( true );
		} catch( final Exception e ){
			notifyAsync_PostConstruct_result.set( false );
		}
	}

	@Callable
	boolean getName_PostConstruct_check(){
		return getName_PostConstruct_result.get();
	}

	@Callable
	boolean getParent_PostConstruct_check(){
		return getParent_PostConstruct_result.get();
	}

	@Callable
	boolean getParentAsFactory_PostConstruct_check(){
		return getParentAsFactory_PostConstruct_result.get();
	}

	@Callable
	boolean getParents_PostConstruct_check(){
		return getParents_PostConstruct_result.get();
	}

	@Callable
	boolean getRemoteAddress_PostConstruct_check(){
		return getRemoteAddress_PostConstruct_result.get();
	}

	@Callable
	boolean show_PostConstruct_check(){
		return show_PostConstruct_result.get();
	}

	@Callable
	boolean hide_PostConstruct_check(){
		return hide_PostConstruct_result.get();
	}

	@Callable
	boolean isShown_PostConstruct_check(){
		return isShown_PostConstruct_result.get();
	}

	@Callable
	boolean isHidden_PostConstruct_check(){
		return isHidden_PostConstruct_result.get();
	}

	@Callable
	boolean isReadOnly_PostConstruct_check(){
		return isReadOnly_PostConstruct_result.get();
	}

	@Callable
	boolean isNonNull_PostConstruct_check(){
		return isNonNull_PostConstruct_result.get();
	}

	@Callable
	boolean isHistorical_PostConstruct_check(){
		return isHistorical_PostConstruct_result.get();
	}

	@Callable
	boolean getPrincipal_PostConstruct_check(){
		return getPrincipal_PostConstruct_result.get();
	}

	@Callable
	boolean getScheduler_PostConstruct_check(){
		return getScheduler_PostConstruct_result.get();
	}

	@Callable
	boolean getSessionId_PostConstruct_check(){
		return getSessionId_PostConstruct_result.get();
	}

	@Callable
	boolean getSubSessionId_PostConstruct_check(){
		return getSubSessionId_PostConstruct_result.get();
	}

	@Callable
	boolean lock_PostConstruct_check(){
		return lock_PostConstruct_result.get();
	}

	@Callable
	boolean unlock_PostConstruct_check(){
		return unlock_PostConstruct_result.get();
	}

	@Callable
	boolean tryLock_PostConstruct_check(){
		return tryLock_PostConstruct_result.get();
	}

	@Callable
	boolean tryLock_timeout_PostConstruct_check(){
		return tryLock_timeout_PostConstruct_result.get();
	}

	@Callable
	boolean isLocked_PostConstruct_check(){
		return isLocked_PostConstruct_result.get();
	}

	@Callable
	boolean isLockedByCurrentThread_PostConstruct_check(){
		return isLockedByCurrentThread_PostConstruct_result.get();
	}

	@Callable
	boolean execute_PostConstruct_check(){
		return execute_PostConstruct_result.get();
	}

	@Callable
	boolean execute_runnable_PostConstruct_check(){
		return execute_runnable_PostConstruct_result.get();
	}

	@Callable
	boolean execute_callable_PostConstruct_check(){
		return execute_callable_PostConstruct_result.get();
	}

	@Callable
	boolean timeout_PostConstruct_check(){
		return timeout_PostConstruct_result.get();
	}

	@Callable
	boolean timeout_runnable_PostConstruct_check(){
		return timeout_runnable_PostConstruct_result.get();
	}

	@Callable
	boolean timeout_callable_PostConstruct_check(){
		return timeout_callable_PostConstruct_result.get();
	}

	@Callable
	boolean interval_PostConstruct_check(){
		return interval_PostConstruct_result.get();
	}

	@Callable
	boolean interval_paramter_PostConstruct_check(){
		return interval_parameter_PostConstruct_result.get();
	}

	@Callable
	boolean interval_runnable_PostConstruct_check(){
		return interval_runnable_PostConstruct_result.get();
	}

	@Callable
	boolean interval_runnable_startAt_PostConstruct_check(){
		return interval_runnable_startAt_PostConstruct_result.get();
	}

	@Callable
	boolean cancel_by_id_PostConstruct_check(){
		return cancel_by_id_PostConstruct_result.get();
	}

	@Callable
	boolean cancel_PostConstruct_check(){
		return cancel_PostConstruct_result.get();
	}

	@Callable
	boolean cancelAll_PostConstruct_check(){
		return cancelAll_PostConstruct_result.get();
	}

	@Callable
	boolean triggerAndWait_PostConstruct_check(){
		return triggerAndWait_PostConstruct_result.get();
	}

	@Callable
	boolean trigger_PostConstruct_check(){
		return trigger_PostConstruct_result.get();
	}

	@Callable
	boolean getParameter_PostConstruct_check(){
		return getParameter_PostConstruct_result.get();
	}

	@Callable
	boolean getParameters_PostConstruct_check(){
		return getParameters_PostConstruct_result.get();
	}

	@Callable
	boolean getParameterMap_PostConstruct_check(){
		return getParameterMap_PostConstruct_result.get();
	}

	@Callable
	boolean getFactoryParameters_PostConstruct_check(){
		return getFactoryParameters_PostConstruct_result.get();
	}

	@Callable
	boolean getAttributes_PostConstruct_check(){
		return getAttributes_PostConstruct_result.get();
	}

	@Callable
	boolean getLocales_PostConstruct_check(){
		return getLocales_PostConstruct_result.get();
	}

	@Callable
	boolean getLocale_PostConstruct_check(){
		return getLocale_PostConstruct_result.get();
	}

	@Callable
	boolean notify_PostConstruct_check(){
		return notify_PostConstruct_result.get();
	}

	@Callable
	boolean notifyAsync_PostConstruct_check(){
		return notifyAsync_PostConstruct_result.get();
	}

	@Callable
	boolean getName_check(){
		return Objects.equals( getName(), "BasicsController" );
	}

	@Callable
	boolean getParent_check(){
		return getParent() == null;
	}

	@Callable
	boolean getParentAsFactory_check(){
		return getParentAsFactory() == null;
	}

	@Callable
	boolean getParents_check(){
		return getParents().isEmpty();
	}

	@Autowired
	BasicsEmptyComponent child;

	@Callable
	boolean getParent_child_check(){
		return child.getParent() == this;
	}

	@Callable
	boolean getParents_child_check(){
		return Objects.deepEquals( child.getParents(), ImmutableSet.of( this ) );
	}

	@Callable
	boolean getRemoteAddress_check(){
		return getRemoteAddress() != null;
	}

	@Callable
	boolean getPrincipal_check(){
		try {
			getPrincipal();
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

	@Callable
	boolean getScheduler_check() {
		return getScheduler() != null;
	}

	@Callable
	boolean getSessionId_check(){
		return getSessionId() != null;
	}

	@Callable
	boolean getSubSessionId_check(){
		return getSubSessionId() != null;
	}

	@Callable
	boolean tryLock_check(){
		if( tryLock() ){
			try {
				return true;
			} finally {
				unlock();
			}
		}
		return false;
	}

	@Callable
	boolean tryLock_timeout_check(){
		if( tryLock( 100, TimeUnit.MILLISECONDS ) ){
			try {
				return true;
			} finally {
				unlock();
			}
		}
		return false;
	}

	@Callable
	boolean show_check(){
		try {
			show();
			return true;
		} catch( Exception e ){
			return false;
		}
	}

	@Callable
	boolean hide_check(){
		try {
			hide();
			return true;
		} catch( Exception e ){
			return false;
		}
	}

	@Callable
	boolean isShown_check(){
		try {
			return isShown();
		} catch( Exception e ){
			return false;
		}
	}

	@Callable
	boolean isHidden_check(){
		try {
			return !isHidden();
		} catch( Exception e ){
			return false;
		}
	}

	@Callable
	boolean isReadOnly_check(){
		try {
			return !isReadOnly();
		} catch( Exception e ){
			return false;
		}
	}

	@Callable
	boolean isNonNull_check(){
		try {
			return !isNonNull();
		} catch( Exception e ){
			return false;
		}
	}

	@Callable
	boolean isHistorical_check(){
		try {
			return !isHistorical();
		} catch( Exception e ){
			return false;
		}
	}

	@Callable
	boolean isLocked_check(){
		return isLocked();
	}

	@Callable
	boolean isLockedByCurrentThread_check(){
		return isLockedByCurrentThread();
	}

	@Callable
	@Unlocked
	boolean isLockedByCurrentThread_unlocked_check(){
		return isLockedByCurrentThread() != true;
	}

	//--------------------------------------------------
	// DESTROY
	//--------------------------------------------------
	@OnDestroy
	void onDestroy(){

	}

	@OnDestroy
	@Unlocked
	void onDestroyUnlocked(){

	}

	//--------------------------------------------------
	// SCALAR METHODS
	//--------------------------------------------------
	@Autowired
	SClass<List<String>> field_scalar_list;
	@Autowired
	SClass<Map<String, String>> field_scalar_map;
	@Autowired
	SArrayNode field_scalar_array_node;
	@Autowired
	SObjectNode field_scalar_object_node;
	@Autowired
	SClass<String[]> field_scalar_array;
	@Autowired
	SClass<String> field_scalar_string;

	@Callable
	boolean scalarSize(){
		// List
		field_scalar_list.set( null );
		if( field_scalar_list.size() != 0 ) return false;

		field_scalar_list.set( Collections.<String>emptyList() );
		if( field_scalar_list.size() != 0 ) return false;

		field_scalar_list.set( Arrays.asList("a", "b") );
		if( field_scalar_list.size() != 2 ) return false;

		// Map
		field_scalar_map.set( null );
		if( field_scalar_map.size() != 0 ) return false;

		field_scalar_map.set( Collections.<String, String>emptyMap() );
		if( field_scalar_map.size() != 0 ) return false;

		final Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		field_scalar_map.set( map );
		if( field_scalar_map.size() != 2 ) return false;

		// ArrayNode
		field_scalar_array_node.set( null );
		if( field_scalar_array_node.size() != 0 ) return false;

		field_scalar_array_node.create();
		if( field_scalar_array_node.size() != 0 ) return false;

		field_scalar_array_node.get().add("a").add("b");
		if( field_scalar_array_node.size() != 2 ) return false;

		// ObjectNode
		field_scalar_object_node.set( null );
		if( field_scalar_object_node.size() != 0 ) return false;

		field_scalar_object_node.create();
		if( field_scalar_object_node.size() != 0 ) return false;

		field_scalar_object_node.get().put("a", "a").put("b", "b");
		if( field_scalar_object_node.size() != 2 ) return false;

		// Array
		field_scalar_array.set( null );
		if( field_scalar_array.size() != 0 ) return false;

		field_scalar_array.set( new String[]{} );
		if( field_scalar_array.size() != 0 ) return false;

		field_scalar_array.set( new String[]{ "a", "b" } );
		if( field_scalar_array.size() != 2 ) return false;

		// String
		field_scalar_string.set( null );
		if( field_scalar_string.size() != 0 ) return false;

		field_scalar_string.set( "a" );
		if( field_scalar_string.size() != 1 ) return false;

		field_scalar_string.set( "ab" );
		if( field_scalar_string.size() != 1 ) return false;

		return true;
	}
	@Callable
	boolean scalarIsEmpty(){
		// List
		field_scalar_list.set( null );
		if( field_scalar_list.isEmpty() != true ) return false;

		field_scalar_list.set( Collections.<String>emptyList() );
		if( field_scalar_list.isEmpty() != true ) return false;

		field_scalar_list.set( Arrays.asList("a", "b") );
		if( field_scalar_list.isEmpty() != false ) return false;

		// Map
		field_scalar_map.set( null );
		if( field_scalar_map.isEmpty() != true ) return false;

		field_scalar_map.set( Collections.<String, String>emptyMap() );
		if( field_scalar_map.isEmpty() != true ) return false;

		final Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		field_scalar_map.set( map );
		if( field_scalar_map.isEmpty() != false ) return false;

		// ArrayNode
		field_scalar_array_node.set( null );
		if( field_scalar_array_node.isEmpty() != true ) return false;

		field_scalar_array_node.create();
		if( field_scalar_array_node.isEmpty() != true ) return false;

		field_scalar_array_node.get().add("a").add("b");
		if( field_scalar_array_node.isEmpty() != false ) return false;

		// ObjectNode
		field_scalar_object_node.set( null );
		if( field_scalar_object_node.isEmpty() != true ) return false;

		field_scalar_object_node.create();
		if( field_scalar_object_node.isEmpty() != true ) return false;

		field_scalar_object_node.get().put("a", "a").put("b", "b");
		if( field_scalar_object_node.isEmpty() != false ) return false;

		// Array
		field_scalar_array.set( null );
		if( field_scalar_array.isEmpty() != true ) return false;

		field_scalar_array.set( new String[]{} );
		if( field_scalar_array.isEmpty() != true ) return false;

		field_scalar_array.set( new String[]{ "a", "b" } );
		if( field_scalar_array.isEmpty() != false ) return false;

		// String
		field_scalar_string.set( null );
		if( field_scalar_string.isEmpty() != true ) return false;

		field_scalar_string.set( "a" );
		if( field_scalar_string.isEmpty() != false ) return false;

		field_scalar_string.set( "ab" );
		if( field_scalar_string.isEmpty() != false ) return false;

		return true;
	}
	@Callable
	boolean scalarIsNull(){
		// List
		field_scalar_list.set( null );
		if( field_scalar_list.isNull() != true ) return false;

		field_scalar_list.set( Collections.<String>emptyList() );
		if( field_scalar_list.isNull() != false ) return false;

		field_scalar_list.set( Arrays.asList("a", "b") );
		if( field_scalar_list.isNull() != false ) return false;

		// Map
		field_scalar_map.set( null );
		if( field_scalar_map.isNull() != true ) return false;

		field_scalar_map.set( Collections.<String, String>emptyMap() );
		if( field_scalar_map.isNull() != false ) return false;

		final Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		field_scalar_map.set( map );
		if( field_scalar_map.isNull() != false ) return false;

		// ArrayNode
		field_scalar_array_node.set( null );
		if( field_scalar_array_node.isNull() != true ) return false;

		field_scalar_array_node.create();
		if( field_scalar_array_node.isNull() != false ) return false;

		field_scalar_array_node.get().add("a").add("b");
		if( field_scalar_array_node.isNull() != false ) return false;

		// ObjectNode
		field_scalar_object_node.set( null );
		if( field_scalar_object_node.isNull() != true ) return false;

		field_scalar_object_node.create();
		if( field_scalar_object_node.isNull() != false ) return false;

		field_scalar_object_node.get().put("a", "a").put("b", "b");
		if( field_scalar_object_node.isNull() != false ) return false;

		// Array
		field_scalar_array.set( null );
		if( field_scalar_array.isNull() != true ) return false;

		field_scalar_array.set( new String[]{} );
		if( field_scalar_array.isNull() != false ) return false;

		field_scalar_array.set( new String[]{ "a", "b" } );
		if( field_scalar_array.isNull() != false ) return false;

		// String
		field_scalar_string.set( null );
		if( field_scalar_string.isNull() != true ) return false;

		field_scalar_string.set( "a" );
		if( field_scalar_string.isNull() != false ) return false;

		field_scalar_string.set( "ab" );
		if( field_scalar_string.isNull() != false ) return false;

		return true;
	}
	@Callable
	boolean scalarIsNotNull(){
		// List
		field_scalar_list.set( null );
		if( field_scalar_list.isNotNull() != false ) return false;

		field_scalar_list.set( Collections.<String>emptyList() );
		if( field_scalar_list.isNotNull() != true ) return false;

		field_scalar_list.set( Arrays.asList("a", "b") );
		if( field_scalar_list.isNotNull() != true ) return false;

		// Map
		field_scalar_map.set( null );
		if( field_scalar_map.isNotNull() != false ) return false;

		field_scalar_map.set( Collections.<String, String>emptyMap() );
		if( field_scalar_map.isNotNull() != true ) return false;

		final Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		field_scalar_map.set( map );
		if( field_scalar_map.isNotNull() != true ) return false;

		// ArrayNode
		field_scalar_array_node.set( null );
		if( field_scalar_array_node.isNotNull() != false ) return false;

		field_scalar_array_node.create();
		if( field_scalar_array_node.isNotNull() != true ) return false;

		field_scalar_array_node.get().add("a").add("b");
		if( field_scalar_array_node.isNotNull() != true ) return false;

		// ObjectNode
		field_scalar_object_node.set( null );
		if( field_scalar_object_node.isNotNull() != false ) return false;

		field_scalar_object_node.create();
		if( field_scalar_object_node.isNotNull() != true ) return false;

		field_scalar_object_node.get().put("a", "a").put("b", "b");
		if( field_scalar_object_node.isNotNull() != true ) return false;

		// Array
		field_scalar_array.set( null );
		if( field_scalar_array.isNotNull() != false ) return false;

		field_scalar_array.set( new String[]{} );
		if( field_scalar_array.isNotNull() != true ) return false;

		field_scalar_array.set( new String[]{ "a", "b" } );
		if( field_scalar_array.isNotNull() != true ) return false;

		// String
		field_scalar_string.set( null );
		if( field_scalar_string.isNotNull() != false ) return false;

		field_scalar_string.set( "a" );
		if( field_scalar_string.isNotNull() != true ) return false;

		field_scalar_string.set( "ab" );
		if( field_scalar_string.isNotNull() != true ) return false;

		return true;
	}
	@Callable
	boolean scalarIndexOf(){
		// List
		field_scalar_list.set( null );
		if( field_scalar_list.indexOf( "b" ) != -1 ) return false;

		field_scalar_list.set( Collections.<String>emptyList() );
		if( field_scalar_list.indexOf( "b" ) != -1 ) return false;

		field_scalar_list.set( Arrays.asList("a", "b") );
		if( field_scalar_list.indexOf( "b" ) != 1 ) return false;

		// Map
		field_scalar_map.set( null );
		if( field_scalar_map.indexOf( "b" ) != -1 ) return false;

		field_scalar_map.set( Collections.<String, String>emptyMap() );
		if( field_scalar_map.indexOf( "b" ) != -1 ) return false;

		final Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		field_scalar_map.set( map );
		if( field_scalar_map.indexOf( "b" ) != -1 ) return false;

		// ArrayNode
		field_scalar_array_node.set( null );
		if( field_scalar_array_node.indexOf( "b" ) != -1 ) return false;

		field_scalar_array_node.create();
		if( field_scalar_array_node.indexOf( "b" ) != -1 ) return false;

		field_scalar_array_node.get().add("a").add("b");
		if( field_scalar_array_node.indexOf( new TextNode( "b" ) ) != 1 ) return false;

		// ObjectNode
		field_scalar_object_node.set( null );
		if( field_scalar_object_node.indexOf( "b" ) != -1 ) return false;

		field_scalar_object_node.create();
		if( field_scalar_object_node.indexOf( "b" ) != -1 ) return false;

		field_scalar_object_node.get().put("a", "a").put("b", "b");
		if( field_scalar_object_node.indexOf( new TextNode( "b" ) ) != -1 ) return false;

		// Array
		field_scalar_array.set( null );
		if( field_scalar_array.indexOf( "b" ) != -1 ) return false;

		field_scalar_array.set( new String[]{} );
		if( field_scalar_array.indexOf( "b" ) != -1 ) return false;

		field_scalar_array.set( new String[]{ "a", "b" } );
		if( field_scalar_array.indexOf( "b" ) != 1 ) return false;

		// String
		field_scalar_string.set( null );
		if( field_scalar_string.indexOf( "b" ) != -1 ) return false;

		field_scalar_string.set( "a" );
		if( field_scalar_string.indexOf( "b" ) != -1 ) return false;

		field_scalar_string.set( "ab" );
		if( field_scalar_string.indexOf( "b" ) != -1 ) return false;

		return true;
	}
	@Callable
	boolean scalarLastIndexOf(){
		// List
		field_scalar_list.set( null );
		if( field_scalar_list.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_list.set( Collections.<String>emptyList() );
		if( field_scalar_list.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_list.set( Arrays.asList("a", "b") );
		if( field_scalar_list.lastIndexOf( "a" ) != 0 ) return false;

		// Map
		field_scalar_map.set( null );
		if( field_scalar_map.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_map.set( Collections.<String, String>emptyMap() );
		if( field_scalar_map.lastIndexOf( "a" ) != -1 ) return false;

		final Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		field_scalar_map.set( map );
		if( field_scalar_map.lastIndexOf( "a" ) != -1 ) return false;

		// ArrayNode
		field_scalar_array_node.set( null );
		if( field_scalar_array_node.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_array_node.create();
		if( field_scalar_array_node.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_array_node.get().add("a").add("b");
		if( field_scalar_array_node.lastIndexOf( new TextNode( "a" ) ) != 0 ) return false;

		// ObjectNode
		field_scalar_object_node.set( null );
		if( field_scalar_object_node.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_object_node.create();
		if( field_scalar_object_node.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_object_node.get().put("a", "a").put("b", "b");
		if( field_scalar_object_node.lastIndexOf( new TextNode( "a" ) ) != -1 ) return false;

		// Array
		field_scalar_array.set( null );
		if( field_scalar_array.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_array.set( new String[]{} );
		if( field_scalar_array.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_array.set( new String[]{ "a", "b" } );
		if( field_scalar_array.lastIndexOf( "a" ) != 0 ) return false;

		// String
		field_scalar_string.set( null );
		if( field_scalar_string.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_string.set( "a" );
		if( field_scalar_string.lastIndexOf( "a" ) != -1 ) return false;

		field_scalar_string.set( "ab" );
		if( field_scalar_string.lastIndexOf( "a" ) != -1 ) return false;

		return true;
	}
	@Callable
	boolean scalarContains(){
		// List
		field_scalar_list.set( null );
		if( field_scalar_list.contains( "b" ) != false ) return false;

		field_scalar_list.set( Collections.<String>emptyList() );
		if( field_scalar_list.contains( "b" ) != false ) return false;

		field_scalar_list.set( Arrays.asList("a", "b") );
		if( field_scalar_list.contains( "b" ) != true ) return false;

		// Map
		field_scalar_map.set( null );
		if( field_scalar_map.contains( "b" ) != false ) return false;

		field_scalar_map.set( Collections.<String, String>emptyMap() );
		if( field_scalar_map.contains( "b" ) != false ) return false;

		final Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		field_scalar_map.set( map );
		if( field_scalar_map.contains( "b" ) != true ) return false;

		// ArrayNode
		field_scalar_array_node.set( null );
		if( field_scalar_array_node.contains( "b" ) != false ) return false;

		field_scalar_array_node.create();
		if( field_scalar_array_node.contains( "b" ) != false ) return false;

		field_scalar_array_node.get().add("a").add("b");
		if( field_scalar_array_node.contains( new TextNode( "b" ) ) != true ) return false;

		// ObjectNode
		field_scalar_object_node.set( null );
		if( field_scalar_object_node.contains( "b" ) != false ) return false;

		field_scalar_object_node.create();
		if( field_scalar_object_node.contains( "b" ) != false ) return false;

		field_scalar_object_node.get().put("a", "a").put("b", "b");
		if( field_scalar_object_node.contains( new TextNode( "b" ) ) != true ) return false;

		// Array
		field_scalar_array.set( null );
		if( field_scalar_array.contains( "b" ) != false ) return false;

		field_scalar_array.set( new String[]{} );
		if( field_scalar_array.contains( "b" ) != false ) return false;

		field_scalar_array.set( new String[]{ "a", "b" } );
		if( field_scalar_array.contains( "a" ) != true ) return false;

		// String
		field_scalar_string.set( null );
		if( field_scalar_string.contains( "b" ) != false ) return false;

		field_scalar_string.set( "a" );
		if( field_scalar_string.contains( "b" ) != false ) return false;

		field_scalar_string.set( "ab" );
		if( field_scalar_string.contains( "b" ) != false ) return false;

		field_scalar_string.set( "ab" );
		if( field_scalar_string.contains( "ab" ) != true ) return false;

		return true;
	}
	@Callable
	boolean scalarContainsAll(){
		// List
		field_scalar_list.set( null );
		if( field_scalar_list.containsAll( Arrays.asList("a", "b") ) != false ) return false;

		field_scalar_list.set( Collections.<String>emptyList() );
		if( field_scalar_list.containsAll( Arrays.asList("a", "b") ) != false ) return false;
		if( field_scalar_list.containsAll( Collections.emptyList() ) != true ) return false;

		field_scalar_list.set( Arrays.asList("a", "b") );
		if( field_scalar_list.containsAll( Arrays.asList("a", "b") ) != true ) return false;

		// Map
		field_scalar_map.set( null );
		if( field_scalar_map.containsAll( Arrays.asList("a", "b") ) != false ) return false;

		field_scalar_map.set( Collections.<String, String>emptyMap() );
		if( field_scalar_map.containsAll( Arrays.asList("a", "b") ) != false ) return false;
		if( field_scalar_map.containsAll( Collections.emptyList() ) != true ) return false;

		final Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		field_scalar_map.set( map );
		if( field_scalar_map.containsAll( Arrays.asList("a", "b") ) != true ) return false;

		// ArrayNode
		field_scalar_array_node.set( null );
		if( field_scalar_array_node.containsAll( Arrays.asList("a", "b") ) != false ) return false;

		field_scalar_array_node.create();
		if( field_scalar_array_node.containsAll( Arrays.asList("a", "b") ) != false ) return false;
		if( field_scalar_array_node.containsAll( Collections.emptyList() ) != true ) return false;

		field_scalar_array_node.get().add("a").add("b");
		if( field_scalar_array_node.containsAll( Arrays.asList(new TextNode( "a" ), new TextNode( "b" ) ) ) != true ) return false;

		// ObjectNode
		field_scalar_object_node.set( null );
		if( field_scalar_object_node.containsAll( Arrays.asList("a", "b") ) != false ) return false;

		field_scalar_object_node.create();
		if( field_scalar_object_node.containsAll( Arrays.asList("a", "b") ) != false ) return false;
		if( field_scalar_object_node.containsAll( Collections.emptyList() ) != true ) return false;

		field_scalar_object_node.get().put("a", "a").put("b", "b");
		if( field_scalar_object_node.containsAll( Arrays.asList(new TextNode( "a" ), new TextNode( "b" ) ) ) != true ) return false;

		// Array
		field_scalar_array.set( null );
		if( field_scalar_array.containsAll( Arrays.asList("a", "b") ) != false ) return false;

		field_scalar_array.set( new String[]{} );
		if( field_scalar_array.containsAll( Arrays.asList("a", "b") ) != false ) return false;
		if( field_scalar_array.containsAll( Collections.emptyList() ) != true ) return false;

		field_scalar_array.set( new String[]{ "a", "b" } );
		if( field_scalar_array.containsAll( Arrays.asList("a", "b") ) != true ) return false;

		// String
		field_scalar_string.set( null );
		if( field_scalar_string.containsAll( Arrays.asList("a", "b") ) != false ) return false;

		field_scalar_string.set( "a" );
		if( field_scalar_string.containsAll( Arrays.asList("a", "b") ) != false ) return false;

		field_scalar_string.set( "ab" );
		if( field_scalar_string.containsAll( Arrays.asList("a", "b") ) != false ) return false;

		field_scalar_string.set( "ab" );
		if( field_scalar_string.containsAll( Arrays.asList("ab") ) != true ) return false;

		return true;
	}
}
