/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package squeue.random;

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
import org.wcardinal.controller.data.SQueue;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class SQueueController extends AbstractController {
	@Constant
	int MAX_COUNT = 1000;

	@Autowired
	SQueue<Integer> queue;

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
			int size = queue.size();
			int capacity = (int) Math.round( 10 + Math.random() * 100 );
			int value = (int) Math.round( Math.random() * 100 );
			final double choice = Math.random();

			if( size <= 0 || 0.5 <= choice ) {
				queue.add( value );
			} else if( 0.25 <= choice ) {
				queue.remove();
			} else {
				queue.setCapacity( capacity );
			}
		}

		if( count.incrementAndGet() < MAX_COUNT ) {
			timeout( "modify", 12 );
		} else {
			check( browser_side_result.get() );
		}
	}

	@Autowired
	SClass<List<Integer>> browser_side_result;

	@Autowired
	@NonNull
	SBoolean check_result;

	@OnChange( "browser_side_result" )
	void check( final List<Integer> data ) {
		if( MAX_COUNT <= count.get() ) {
			System.out.println( "Check server: " + queue.toString() );
			System.out.println( "Check browser: " + data.toString() );
			if( queue.equals( data ) ) {
				check_result.set( true );
			}
		}
	}
}
