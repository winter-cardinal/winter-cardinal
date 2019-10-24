/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SROQueue;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.controller.data.annotation.Soft;
import org.wcardinal.util.thread.Unlocker;

@Controller
public class BasicsSoftQueueController {
	@Autowired
	ControllerFacade facade;

	@Autowired @Soft
	SROQueue<String> field;

	@Autowired @NonNull
	SBoolean initial_check_result1;

	@Autowired @NonNull
	SBoolean check_result1;

	@Autowired @NonNull
	SBoolean initial_check_result2;

	@Autowired @NonNull
	SBoolean check_result2;

	@Autowired @NonNull
	SBoolean check_result3;

	Queue<String> queueOf( final String... entries ){
		final Queue<String> result = new LinkedList<>();
		for( int i=0; i < entries.length; ++i ){
			result.add(entries[ i ]);
		}
		return result;
	}

	@OnCreate
	void init() {
		facade.interval( "check1", 100 );
		field.add( "John" );
		initial_check_result1.set( field.peek() != null );
	}

	@OnTime
	void check1(){
		if( field.isEmpty() ) {
			check_result1.set( true );
			facade.cancel();
			field.add( "John" );
			try( Unlocker unlocker = field.lock() ) {
				for( final String value: field ) {
					if( value != null ) {
						initial_check_result2.set( true );
					}
				}
			}
			facade.interval( "check2", 100 );
		}
	}

	@OnTime
	void check2(){
		try( Unlocker unlocker = field.lock() ) {
			if( field.isEmpty() != true ) return;
		}

		check_result2.set( field.isEmpty() );
		facade.cancel();
		field.capacity( 10 );
		for( int i=0; i<10; ++i ) {
			field.addAll( queueOf( "John" ) );
		}
		facade.interval( "check3", 100 );
	}

	@OnTime
	void check3() {
		try( Unlocker unlocker = field.lock() ) {
			if( field.isEmpty() != true ) return;
		}

		check_result3.set( field.isEmpty() );
		facade.cancel();
	}
}
