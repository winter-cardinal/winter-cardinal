/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.longrun.polling;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.SString;

@Controller(protocols="polling-100")
public class LongRunController {
	@Autowired
	ControllerFacade facade;

	@Autowired
	SString field_string;

	@Autowired
	SList<String> field_list;

	@OnCreate
	void onCreate(){
		facade.interval("update", 0, 1000);
	}

	@OnTime
	@Locked
	void update(){
		final String value = String.valueOf(System.currentTimeMillis());
		field_string.set( value );
		field_list.add( value );
		if( 10 < field_list.size() ) field_list.remove( 0 );
	}
}
