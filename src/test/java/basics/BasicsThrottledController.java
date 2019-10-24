/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractComponent;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnNotice;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.annotation.Throttled;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SLong;

@Controller
public class BasicsThrottledController extends AbstractComponent {
	final Logger logger = LoggerFactory.getLogger(BasicsThrottledController.class);

	final AtomicLong startTime = new AtomicLong();
	final AtomicLong count = new AtomicLong( 0 );

	@Autowired
	SLong elapsed;

	@Autowired
	SBoolean ended;

	@OnCreate
	void onCreate(){
		ended.set(false);
	}

	@Callable
	void start(){
		count.set( 0 );
		startTime.set(System.currentTimeMillis());
		interval( "update", 0, 300 );
	}

	@OnTime
	@Throttled( interval=1000 )
	void update(){
		if( isLockedByCurrentThread() != true ) {
			if( count.getAndIncrement() == 1 ){
				ended.set(true);
				cancel();
			}

			elapsed.set( Math.round((System.currentTimeMillis() - startTime.get())/500.0) );
		}
	}

	@Autowired
	SLong notified;

	@Callable
	void notice(){
		notified.set( 0L );
		this.notify("warning");
	}

	@OnNotice( "warning" )
	@Throttled( interval=500 )
	void onNotice(){
		if( isLockedByCurrentThread() == true ) {
			notified.incrementAndGet();
		}
	}
}
