/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.random;

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
		int index = (int) Math.floor( size * Math.random() );
		if( size <= 0 ) {
			field_list.add( 0 );
		} else {
			double random = Math.random();
			int value = (int) Math.round(Math.random()*100);
			if( 0.666 <= random ) {
				field_list.add( index, value );
			} else if( 0.333 <= random ){
				field_list.remove( index );
			} else {
				field_list.set( index, value );
			}
		}

		if( count.incrementAndGet() < MAX_COUNT ) {
			timeout( "modify", 12 );
		} else {
			check();
		}
	}

	@Autowired
	SClass<List<Integer>> browser_side_result;

	@Autowired
	@NonNull
	SBoolean check_result;

	@OnChange( "browser_side_result" )
	void check() {
		if( MAX_COUNT <= count.get() ) {
			System.out.println( "Server : "+field_list.toString() );
			System.out.println( "Browser: "+browser_side_result.toString() );
			if( field_list.equals( browser_side_result.get() ) ) {
				check_result.set( true );
			}
		}
	}
}
