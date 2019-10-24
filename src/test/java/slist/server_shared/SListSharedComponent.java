/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.server_shared;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractComponent;
import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.annotation.SharedComponent;
import org.wcardinal.controller.data.SList;

@SharedComponent
public class SListSharedComponent extends AbstractComponent {
	@Constant
	final int COUNT_MAX = 10;

	@Autowired
	SList<Integer> field_list;

	@OnCreate
	void init() {
		interval( "add", 0, 1000 );
	}

	@OnTime
	@Locked
	void add() {
		if( field_list.size() < COUNT_MAX ) {
			field_list.add( field_list.size() );
		} else {
			field_list.remove( 0 );
			field_list.add( field_list.get( field_list.size()-1 ) + 1 );
		}
	}
}
