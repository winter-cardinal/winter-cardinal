/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SDouble;
import org.wcardinal.controller.data.SFloat;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.SLong;

@Controller
public class BasicsNumberController {
	//--------------------------------------------
	// LONG
	//--------------------------------------------
	@Autowired
	SLong field_long;
	@OnCreate
	void long_onCreate(){
		field_long.set( 0L );
	}
	@OnChange( "field_long" )
	void long_onChange(){
		field_long.incrementAndGet();
	}
	@Callable
	long long_addAndGet( long delta ){
		return field_long.addAndGet( delta );
	}
	@Callable
	long long_getAndAdd( long delta ){
		return field_long.getAndAdd( delta );
	}
	@Callable
	boolean long_decrementAndGet_check(){
		field_long.set( 1L );
		return field_long.decrementAndGet() == 0L;
	}
	@Callable
    boolean long_getAndDecrement_check() {
		field_long.set( 1L );
		return field_long.getAndDecrement() == 1L && field_long.get() == 0L;
    }
	@Callable
    boolean long_getAndIncrement_check() {
		field_long.set( 1L );
		return field_long.getAndIncrement() == 1L && field_long.get() == 2L;
    }
	@Callable
    boolean long_incrementAndGet_check() {
		field_long.set( 1L );
		return field_long.incrementAndGet() == 2L;
    }
	@Callable
    boolean long_compareTo_check(){
		field_long.set( 1L );
		return field_long.compareTo( 1L ) == 0 && field_long.compareTo( 0L ) > 0 && field_long.compareTo( 2L ) < 0 && field_long.compareTo( null ) > 0;
    }
	@Callable
    boolean long_compareTo_null_check(){
		field_long.set( null );
		return field_long.compareTo( 1L ) < 0 && field_long.compareTo( null ) == 0;
    }
	@Callable
    boolean long_getAndAdd_null_1_check() {
		field_long.set( null );
		return field_long.getAndAdd( 1L ) == null && field_long.get() == null;
    }
	@Callable
    boolean long_addAndGet_null_1_check(){
		field_long.set( null );
		return field_long.addAndGet( 1L ) == null;
    }
	@Callable
    boolean long_getAndAdd_null_2_check() {
		try {
			field_long.getAndAdd( null );
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
    }
	@Callable
    boolean long_addAndGet_null_2_check(){
		try {
			field_long.addAndGet( null );
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
    }

	//--------------------------------------------
	// FLOAT
	//--------------------------------------------
	@Autowired
	SFloat field_float;
	@OnCreate
	void float_onCreate(){
		field_float.set( 0.0F );
	}
	@OnChange( "field_float" )
	void float_onChange(){
		field_float.incrementAndGet();
	}
	@Callable
	float float_addAndGet( float delta ){
		return field_float.addAndGet( delta );
	}
	@Callable
	float float_getAndAdd( float delta ){
		return field_float.getAndAdd( delta );
	}
	@Callable
	boolean float_decrementAndGet_check(){
		field_float.set( 1.0F );
		return field_float.decrementAndGet() == 0.0F;
	}
	@Callable
    boolean float_getAndDecrement_check() {
		field_float.set( 1.0F );
		return field_float.getAndDecrement() == 1.0F && field_float.get() == 0.0F;
    }
	@Callable
    boolean float_getAndIncrement_check() {
		field_float.set( 1.0F );
		return field_float.getAndIncrement() == 1.0F && field_float.get() == 2.0F;
    }
	@Callable
    boolean float_incrementAndGet_check() {
		field_float.set( 1.0F );
		return field_float.incrementAndGet() == 2.0F;
    }
	@Callable
    boolean float_compareTo_check(){
		field_float.set( 1.0F );
		return field_float.compareTo( 1.0F ) == 0 && field_float.compareTo( 0.0F ) > 0 && field_float.compareTo( 2.0F ) < 0 && field_float.compareTo( null ) > 0;
    }
	@Callable
    boolean float_compareTo_null_check(){
		field_float.set( null );
		return field_float.compareTo( 1.0F ) < 0 && field_float.compareTo( null ) == 0;
    }
	@Callable
    boolean float_getAndAdd_null_1_check() {
		field_float.set( null );
		return field_float.getAndAdd( 1.0F ) == null && field_float.get() == null;
    }
	@Callable
    boolean float_addAndGet_null_1_check(){
		field_float.set( null );
		return field_float.addAndGet( 1.0F ) == null;
    }
	@Callable
    boolean float_getAndAdd_null_2_check() {
		try {
			field_float.getAndAdd( null );
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
    }
	@Callable
    boolean float_addAndGet_null_2_check(){
		try {
			field_float.addAndGet( null );
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
    }

	//--------------------------------------------
	// DOUBLE
	//--------------------------------------------
	@Autowired
	SDouble field_double;
	@OnCreate
	void double_onCreate(){
		field_double.set( 0.0 );
	}
	@OnChange( "field_double" )
	void double_onChange(){
		field_double.incrementAndGet();
	}
	@Callable
	double double_addAndGet( double delta ){
		return field_double.addAndGet( delta );
	}
	@Callable
	double double_getAndAdd( double delta ){
		return field_double.getAndAdd( delta );
	}
	@Callable
	boolean double_decrementAndGet_check(){
		field_double.set( 1.0 );
		return field_double.decrementAndGet() == 0.0;
	}
	@Callable
    boolean double_getAndDecrement_check() {
		field_double.set( 1.0 );
		return field_double.getAndDecrement() == 1.0 && field_double.get() == 0.0;
    }
	@Callable
    boolean double_getAndIncrement_check() {
		field_double.set( 1.0 );
		return field_double.getAndIncrement() == 1.0 && field_double.get() == 2.0;
    }
	@Callable
    boolean double_incrementAndGet_check() {
		field_double.set( 1.0 );
		return field_double.incrementAndGet() == 2.0;
    }
	@Callable
    boolean double_compareTo_check(){
		field_double.set( 1.0 );
		return field_double.compareTo( 1.0 ) == 0 && field_double.compareTo( 0.0 ) > 0 && field_double.compareTo( 2.0 ) < 0 && field_double.compareTo( null ) > 0;
    }
	@Callable
    boolean double_compareTo_null_check(){
		field_double.set( null );
		return field_double.compareTo( 1.0 ) < 0 && field_double.compareTo( null ) == 0;
    }
	@Callable
    boolean double_getAndAdd_null_1_check() {
		field_double.set( null );
		return field_double.getAndAdd( 1.0 ) == null && field_double.get() == null;
    }
	@Callable
    boolean double_addAndGet_null_1_check(){
		field_double.set( null );
		return field_double.addAndGet( 1.0 ) == null;
    }
	@Callable
    boolean double_getAndAdd_null_2_check() {
		try {
			field_double.getAndAdd( null );
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
    }
	@Callable
    boolean double_addAndGet_null_2_check(){
		try {
			field_double.addAndGet( null );
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
    }

	//--------------------------------------------
	// INT
	//--------------------------------------------
	@Autowired
	SInteger field_int;
	@OnCreate
	void int_onCreate(){
		field_int.set( 0 );
	}
	@OnChange( "field_int" )
	void int_onChange(){
		field_int.incrementAndGet();
	}
	@Callable
	int int_addAndGet( int delta ){
		return field_int.addAndGet( delta );
	}
	@Callable
	int int_getAndAdd( int delta ){
		return field_int.getAndAdd( delta );
	}
	@Callable
	boolean int_decrementAndGet_check(){
		field_int.set( 1 );
		return field_int.decrementAndGet() == 0;
	}
	@Callable
    boolean int_getAndDecrement_check() {
		field_int.set( 1 );
		return field_int.getAndDecrement() == 1 && field_int.get() == 0;
    }
	@Callable
    boolean int_getAndIncrement_check() {
		field_int.set( 1 );
		return field_int.getAndIncrement() == 1 && field_int.get() == 2;
    }
	@Callable
    boolean int_incrementAndGet_check() {
		field_int.set( 1 );
		return field_int.incrementAndGet() == 2;
    }
	@Callable
    boolean int_compareTo_check(){
		field_int.set( 1 );
		return field_int.compareTo( 1 ) == 0 && field_int.compareTo( 0 ) > 0 && field_int.compareTo( 2 ) < 0 && field_int.compareTo( null ) > 0;
    }
	@Callable
    boolean int_compareTo_null_check(){
		field_int.set( null );
		return field_int.compareTo( 1 ) < 0 && field_int.compareTo( null ) == 0;
    }
	@Callable
    boolean int_getAndAdd_null_1_check() {
		field_int.set( null );
		return field_int.getAndAdd( 1 ) == null && field_int.get() == null;
    }
	@Callable
    boolean int_addAndGet_null_1_check(){
		field_int.set( null );
		return field_int.addAndGet( 1 ) == null;
    }
	@Callable
    boolean int_getAndAdd_null_2_check() {
		try {
			field_int.getAndAdd( null );
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
    }
	@Callable
    boolean int_addAndGet_null_2_check(){
		try {
			field_int.addAndGet( null );
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
    }
}
