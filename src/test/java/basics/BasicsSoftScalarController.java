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
import org.wcardinal.controller.data.SClass;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.controller.data.annotation.Soft;

@Controller
public class BasicsSoftScalarController {
	@Autowired
	ControllerFacade facade;

	@Autowired @Soft
	SClass<String> field;

	@Autowired @NonNull
	SBoolean initial_check_result;

	@Autowired @NonNull
	SBoolean check_result;

	@OnCreate
	void init(){
		facade.interval( "check", 100 );
		field.set( "John" );
		initial_check_result.set( field.get() != null );
	}

	@OnTime
	void check(){
		if( field.get() == null ) {
			check_result.compareAndSet(false, true);
		}
	}
}
