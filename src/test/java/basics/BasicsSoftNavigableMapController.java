/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SNavigableMap;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.controller.data.annotation.Soft;

@Controller
public class BasicsSoftNavigableMapController {
	@Autowired
	ControllerFacade facade;

	@Autowired @Soft
	SNavigableMap<String> field;

	@Autowired @NonNull
	SBoolean initial_check_result1;

	@Autowired @NonNull
	SBoolean check_result1;

	@Autowired @NonNull
	SBoolean initial_check_result2;

	@Autowired @NonNull
	SBoolean check_result2;

	@OnCreate
	void init(){
		facade.interval( "check1", 100 );
		field.put( "name1", "John" );
		initial_check_result1.set( ! field.isEmpty() );
	}

	@OnTime
	void check1(){
		if( field.get( "name1" ) == null ) {
			check_result1.set( true );
			facade.cancel();
			field.put( "name1", "John" );
			field.put( "name2", "John" );
			initial_check_result2.set( ! field.isEmpty() );
			facade.interval( "check2", 100 );
		}
	}

	@OnTime
	void check2(){
		if( field.isEmpty() ) {
			check_result2.set( true );
			facade.cancel();
		}
	}
}
