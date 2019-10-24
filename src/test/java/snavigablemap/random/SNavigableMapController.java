/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package snavigablemap.random;

import java.util.Iterator;
import java.util.Map;
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
import org.wcardinal.controller.data.SNavigableMap;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class SNavigableMapController extends AbstractController {
	@Constant
	int MAX_COUNT = 1000;

	@Autowired
	SNavigableMap<Integer> field_map;

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
			int size = field_map.size();
			int index = (int) Math.floor( size * Math.random() );
			int value = (int) Math.round(Math.random()*100);

			if( size <= 0 ) {
				field_map.put( "0", 0 );
			} else {
				final double choice = Math.random();
				if( 0.5 <= choice ) {
					field_map.put( String.valueOf( value ), value );
				} else {
					final Iterator<String> itr = field_map.keySet().iterator();
					for( int j=0; j<index; ++j ) itr.next();
					itr.remove();
				}
			}
		}

		if( count.incrementAndGet() < MAX_COUNT ) {
			timeout( "modify", 12 );
		}
	}

	@Autowired
	SClass<Map<String, Integer>> check;

	@Autowired
	@NonNull
	SBoolean check_result;

	@OnChange( "check" )
	void check( Map<String, Integer> data ) {
		if( MAX_COUNT <= count.get() ) {
			System.out.println( "Check server: "+field_map.toString() );
			System.out.println( "Check browser: "+data.toString() );
			if( field_map.equals( data ) ) {
				check_result.set( true );
			}
		}
	}
}
