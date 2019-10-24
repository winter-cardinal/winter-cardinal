/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package smovablelist.random;

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
import org.wcardinal.controller.data.SMovableList;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class SMovableListController extends AbstractController {
	@Constant
	int MAX_COUNT = 1000;

	@Autowired
	SMovableList<Integer> list;

	final AtomicLong count = new AtomicLong(  0L );

	@Callable
	void start() {
		timeout( "modify",  0 );
	}

	@OnTime
	@Locked
	void modify() {
		int ocount = (int) Math.floor( 10 * Math.random() ) + 1;

		for( int i=0; i<ocount; ++i ) {
			int size = list.size();
			int newIndex = (int) Math.floor( size * Math.random() );
			int oldIndex = (int) Math.floor( size * Math.random() );
			int value = (int) Math.round(Math.random()*100);

			if( size <= 0 ) {
				list.add( 0 );
			} else {
				final double choice = Math.random();
				if( 0.75 <= choice ) {
					list.add( newIndex, value );
				} else if( 0.5 <= choice ){
					list.remove( newIndex );
				} else if( 0.25 <= choice ) {
					list.set( newIndex, value );
				} else {
					list.move( newIndex, oldIndex );
				}
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
			System.out.println( "Check server: "+list.toString() );
			System.out.println( "Check browser: "+browser_side_result.toString() );
			if( list.equals( browser_side_result.get() ) ) {
				check_result.set( true );
			}
		}
	}
}
