/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.SMap;
import org.wcardinal.controller.data.SMovableList;
import org.wcardinal.controller.data.SNavigableMap;
import org.wcardinal.controller.data.SROQueue;
import org.wcardinal.controller.data.SString;

@Controller
public class BasicsReducedEventController {
	@Autowired
	SString field_sstring;

	@Autowired
	SList<String> field_slist;

	@Autowired
	SMap<String> field_smap;

	@Autowired
	SNavigableMap<String> field_snavigablemap;

	@Autowired
	SROQueue<String> field_sroqueue;

	@OnCreate
	void init_sroqueue(){
		field_sroqueue.capacity( 3 );
	}

	@Callable
	void sroqueue_populate(){
		field_sroqueue.add( "a" );
		field_sroqueue.add( "b" );
		field_sroqueue.remove();
		field_sroqueue.add( "c" );
		field_sroqueue.add( "d" );
		field_sroqueue.add( "e" );
		field_sroqueue.remove();
	}

	@Autowired
	SMovableList<String> field_smovablelist;
}
