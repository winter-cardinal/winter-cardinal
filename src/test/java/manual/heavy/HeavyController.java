/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.heavy;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.SString;

@Controller
public class HeavyController {
	@Autowired
	ControllerFacade facade;

	@Autowired
	SString field_string;

	@Autowired
	SList<String> field_list;

	@Autowired
	ComponentFactory<HeavySeries> series;

	@OnCreate
	void onCreate(){
		facade.interval("update", 10000);
	}

	@OnTime
	@Locked
	void update(){
		final String value = String.valueOf(System.currentTimeMillis());
		field_string.set( value );
		field_list.add( value );
		if( 10 < field_list.size() ) field_list.remove( 0 );

		final long startTime = System.currentTimeMillis();
		series.clear();
		for( int n=0; n<10; ++n ) {
			series.create();
		}
		final long endTime = System.currentTimeMillis();
		System.out.println( "Took "+(endTime-startTime) );
	}
}
