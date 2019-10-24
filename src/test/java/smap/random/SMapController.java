/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package smap.random;

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
import org.wcardinal.controller.data.SMap;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class SMapController extends AbstractController {
	@Constant
	int MAX_COUNT = 1000;

	@Autowired
	SMap<Integer> map;

	final AtomicLong count = new AtomicLong( 0L );

	@Callable
	void start() {
		timeout( "modify",  0 );
	}

	@OnTime
	@Locked
	void modify() {
		int ocount = (int) Math.floor( 10 * Math.random() ) + 1;

		for( int i=0; i<ocount; ++i ) {
			int size = map.size();
			int index = (int) Math.floor( size * Math.random() );
			String key = String.valueOf( Math.round(Math.random()*100) );
			int value = (int) Math.round(Math.random()*100);

			if( size <= 0 ) {
				map.put( key, value );
			} else {
				final double choice = Math.random();
				if( 0.5 <= choice ) {
					map.put( key, value );
				} else {
					final Iterator<String> itr = map.keySet().iterator();
					for( int j=0; j<index; ++j ) itr.next();
					itr.remove();
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
	SClass<Map<String, Integer>> browser_side_result;

	@Autowired
	@NonNull
	SBoolean check_result;

	@OnChange( "browser_side_result" )
	void check() {
		if( MAX_COUNT <= count.get() ) {
			final Map<String, Integer> data = browser_side_result.get();
			if( data != null ) {
				System.out.println( "Check server: "+map.toString() );
				System.out.println( "Check browser: "+data.toString() );
				if( map.equals( data ) ) {
					check_result.set( true );
					this.cancel();
				}
			}
		}
	}
}
