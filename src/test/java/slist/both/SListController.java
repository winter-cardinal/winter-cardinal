/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.both;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SClass;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class SListController extends AbstractController {
	@Constant
	int MAX_COUNT = 1000;

	@Autowired
	SList<Integer> field_list;

	final AtomicLong count = new AtomicLong(  0L );

	@Callable
	void start() {
		timeout( "modify",  0 );
	}

	@OnTime
	@Locked
	void modify() {
		int size = field_list.size();
		if( size <= 0 ) {
			field_list.add( 0 );
		} else if( 0.5 <= Math.random() ) {
			field_list.add( field_list.get( size-1 ) + (int) Math.round(Math.random()*10) + 1 );
		} else {
			field_list.remove( (int) Math.floor( size * Math.random() ) );
		}

		if( count.incrementAndGet() < MAX_COUNT ) {
			timeout( "modify", 12 );
		}
	}

	@Autowired
	SClass<List<Integer>> check;

	@Autowired
	@NonNull
	SBoolean check_result;

	@OnChange( "check" )
	void check( List<Integer> data ) {
		if( MAX_COUNT <= count.get() ) {
			System.out.println( "check server: "+field_list.toString() );
			System.out.println( "check browser: "+data.toString() );
			if( field_list.equals( data ) ) {
				check_result.set( true );
			}
		}
	}
}
