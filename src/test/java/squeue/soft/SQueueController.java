/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package squeue.soft;

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
import org.wcardinal.controller.data.annotation.Soft;

@Controller
public class SQueueController extends AbstractController {
	@Constant
	int MAX_COUNT = 10;

	@Autowired @Soft
	SQueue<Integer> field_queue;

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
			int size = field_queue.size();
			int capacity = (int) Math.round( 10 + Math.random() * 100 );
			int value = (int) Math.round( Math.random() * 100 );
			final double choice = Math.random();

			if( size <= 0 || 0.5 <= choice ) {
				field_queue.add( value );
			} else if( 0.25 <= choice ) {
				field_queue.remove();
			} else {
				field_queue.setCapacity( capacity );
			}
		}

		if( count.incrementAndGet() < MAX_COUNT ) {
			timeout( "modify", 1000 );
		}
	}

	@Autowired
	SClass<List<Integer>> check;

	@Autowired
	@NonNull
	SBoolean check_result;

	@OnChange( "check" )
	void check( final List<Integer> data ) {
		if( MAX_COUNT <= count.get() ) {
			System.out.println( "Check server: "+field_queue.toString() );
			System.out.println( "Check browser: "+data.toString() );
			if( field_queue.equals( data ) || field_queue.isEmpty() ) {
				check_result.set( true );
			}
		}
	}
}
