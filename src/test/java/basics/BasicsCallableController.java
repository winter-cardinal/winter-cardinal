/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.Unlocked;

@Controller
public class BasicsCallableController {
	@Callable
	String callable( final String name ){
		return "Hello, "+name;
	}

	@Callable
	ObjectNode callable( final ObjectNode data ){
		final String name = data.get("name").asText();
		data.put("name", name + name);
		return data;
	}

	@Callable
	ArrayNode callable( final ArrayNode data ){
		final String name = data.get( 0 ).asText();
		data.add( name + name );
		data.remove( 0 );
		return data;
	}

	@Callable
	double callable_double( final double value ){
		return value * 2;
	}

	@Callable
	Double callable_double_object( final Double value ){
		return value * 2;
	}

	@Callable
	float callable_float( final float value ){
		return value * 2;
	}

	@Callable
	Float callable_float_object( final Float value ){
		return value * 2;
	}

	@Callable
	int callable_int( final int value ){
		return value * 2;
	}

	@Callable
	Integer callable_int_object( final Integer value ){
		return value * 2;
	}

	@Callable
	short callable_short( final short value ){
		return (short) (value * 2);
	}

	@Callable
	Short callable_short_object( final Short value ){
		return (short) (value * 2);
	}

	@Callable
	byte callable_byte( final byte value ){
		return (byte) (value * 2);
	}

	@Callable
	Byte callable_byte_object( final Byte value ){
		return (byte) (value * 2);
	}

	@Callable
	boolean callable_boolean( final boolean value ){
		return !value;
	}

	@Callable
	Boolean callable_boolean_object( final Boolean value ){
		return !value;
	}

	@Callable
	int callable_multiple( final int a, final int b, final int c ){
		return a + b + c;
	}

	@Callable
	int callable_exception(){
		throw new RuntimeException("exception for test");
	}

	@Callable
	void callable_void(){}

	@Callable
	@Unlocked
	void callable_slow(){
		long stime = System.currentTimeMillis();
		while( System.currentTimeMillis() - stime < 1500 ) {
			try {
				Thread.sleep( 1500 );
			} catch( final Exception e ){

			}
		}
	}

	@Callable
	static boolean callable_static(){
		return true;
	}

	@Callable
	@Unlocked
	static boolean callable_static_unlocked(){
		return true;
	}

	@Callable
	@Locked
	static boolean callable_static_locked(){
		return true;
	}

	@Callable
	boolean callable_class( final BasicsCallableArgumentClass argument ){
		return ( argument != null && Objects.equals( argument.field, "Cardinal" ) );
	}

	@Callable
	boolean callable_2_classes( final BasicsCallableArgumentClass argument0, final BasicsCallableArgumentClass argument1 ){
		return ( argument0 != null && Objects.equals( argument0.field, "Cardinal" ) &&
				 argument1 != null && Objects.equals( argument1.field, "Cardinal" ) );
	}

	@Callable
	boolean callable_class_array( final List<BasicsCallableArgumentClass> arguments ) {
		if( arguments == null || arguments.size() != 2 ) return false;

		for( BasicsCallableArgumentClass argument: arguments ) {
			if( argument == null || Objects.equals( argument.field, "Cardinal" ) != true ) return false;
		}

		return true;
	}

	@Callable
	boolean callable_class_incompatible( final BasicsCallableArgumentClass argument ){
		return false;
	}

	@Callable
	boolean callable_class_incompatible_override( final BasicsCallableArgumentClassIncompatible argument ) {
		return false;
	}

	@Callable
	boolean callable_class_incompatible_override( final BasicsCallableArgumentClass argument ) {
		return true;
	}
}
