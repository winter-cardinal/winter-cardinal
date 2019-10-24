/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnNotice;
import org.wcardinal.controller.data.SInteger;

@Controller
public class BasicsNotificationController {
	@Autowired
	ControllerFacade facade;

	@Autowired
	BasicsNotificationComponent foo;

	@OnCreate
	void onCreate(){
		facade.notify("foo", 1);
	}

	@Callable
	void notify( final int parameter ){
		facade.notify("foo", parameter);
		foo.facade.notify("bar", parameter);
	}

	@Autowired
	SInteger field_foo;

	@OnNotice( "foo" )
	void onNoticeFoo( final int parameter ){
		field_foo.set( parameter );
	}

	@Autowired
	SInteger field_foo_bar;

	@OnNotice( "foo.bar" )
	void onNoticeFooBar( final int parameter ){
		field_foo_bar.set( parameter );
	}
}
