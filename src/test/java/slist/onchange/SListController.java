/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.onchange;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SClass;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class SListController extends AbstractController {
	@Constant
	int MAX_COUNT = 100;

	@Autowired
	SList<Integer> field_list;

	@OnChange( "field_list" )
	@Locked
	void add() {
		int size = field_list.size();
		if( size <= 0 ) {
			field_list.add( 0 );
		} else if( 0.5 <= Math.random() ) {
			field_list.add( field_list.get( size-1 ) + 1 );
		} else {
			field_list.remove( (int) Math.floor( size * Math.random() ) );
		}
	}

	@Autowired
	SClass<List<Integer>> check;

	@Autowired
	@NonNull
	SBoolean check_result;

	@OnChange( "check" )
	void check( List<Integer> data ) {
		System.out.println( "check server: "+field_list.toString() );
		System.out.println( "check browser: "+data.toString() );
		if( field_list.equals( data ) ) {
			check_result.set( true );
		}
	}
}
