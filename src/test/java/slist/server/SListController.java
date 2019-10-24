/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.server;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SList;

@Controller
public class SListController extends AbstractController {
	@Constant
	final int COUNT_MAX = 100;

	@Autowired
	SList<Integer> field_list;

	@OnCreate
	void init() {
		interval( "add", 0, 10 );
	}

	@OnTime
	@Locked
	void add() {
		if( field_list.size() < COUNT_MAX ) {
			field_list.add( field_list.size() );
		}
	}
}
