/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.data.SString;

@Controller(protocols="polling-100")
public class BasicsPollingController {
	@Autowired
	SString field_string;

	@OnChange( "field_string" )
	void onChangeFieldString( final String value ){
		this.field_string.set( "Hello, " + value );
	}

	@Callable
	String hello( final String name ) {
		return "Hello, "+name;
	}
}
