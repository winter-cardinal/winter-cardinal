/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.annotation.Tracked;
import org.wcardinal.controller.data.SInteger;

@Controller
public class BasicsIntervalController extends AbstractController {
	//-----------------------------------------
	// INTERVAL
	//-----------------------------------------
	@Callable
	void start(){
		count.set( 0 );
		total.set( 0 );
		interval("a", 0, 100, 1);
	}

	@Callable
	void restart(){
		interval("b", 100);
	}

	@Autowired
	SInteger count;

	@Autowired
	SInteger total;

	@OnTime
	void a( final int number ){
		count.getAndIncrement();
		total.set(total.get()+number);
		if( total.get() == 3 || total.get() == 9 ) {
			cancelAll();
			total.set( total.get() * 2 );
		}
	}

	@OnTime
	@Locked
	void b(){
		a( 1 );
	}

	//-----------------------------------------
	// TRACKED INTERVAL
	//-----------------------------------------
	@Callable
	void startTracked(){
		tracked_count.set( 0 );
		tracked_total.set( 0 );
		interval("tracked-a", 0, 100, 1);
	}

	@Callable
	void restartTracked(){
		interval("tracked-b", 100);
	}

	@Autowired
	SInteger tracked_count;

	@Autowired
	SInteger tracked_total;

	@OnTime( "tracked-a" )
	@Tracked
	void trackedA( final int number ){
		tracked_count.getAndIncrement();
		tracked_total.set(tracked_total.get()+number);
		if( tracked_total.get() == 3 || tracked_total.get() == 9 ) {
			cancelAll();
			tracked_total.set( tracked_total.get() * 2 );
		}
	}

	@OnTime( "tracked-b" )
	@Locked
	@Tracked
	void trackedB(){
		trackedA( 1 );
	}

	//-----------------------------------------
	// INTERVAL RUNNABLE
	//-----------------------------------------
	@Callable
	void start_runnable_long(){
		interval(new Runnable(){
			@Override
			public void run() {
				if( count.incrementAndGet() == 9 ) {
					cancel();
				}
			}
		}, 100);
	}

	@Callable
	void start_runnable_long_long(){
		interval(new Runnable(){
			@Override
			public void run() {
				if( count.incrementAndGet() == 12 ) {
					cancel();
				}
			}
		}, 0, 100);
	}

	@Callable
	void start_runnable_isCanceled(){
		interval(new Runnable(){
			@Override
			public void run() {
				if( count.incrementAndGet() == 15 ) {
					if( isCanceled() != true ){
						count.incrementAndGet();
					}
					cancel();
					if( isCanceled() == true ){
						count.incrementAndGet();
					}
				}
			}
		}, 0, 100);
	}
}
