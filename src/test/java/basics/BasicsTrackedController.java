/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.util.concurrent.Uninterruptibles;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.annotation.Throttled;
import org.wcardinal.controller.annotation.Tracked;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.SLong;

@Controller
public class BasicsTrackedController extends AbstractController {
	static final Logger logger = LoggerFactory.getLogger(BasicsTrackedController.class);

	//----------------------------------------------
	// NORMAL CASE
	//----------------------------------------------
	@Autowired
	SLong target;

	@Autowired
	SList<Long> results;

	@OnChange("target") @Unlocked @Tracked
	void onChangeTarget( final Long value ){
		for( int i=0; i<20; ++i ) {
			Uninterruptibles.sleepUninterruptibly(50, TimeUnit.MILLISECONDS);
			if( ! isHeadCall() ) {
				logger.info("onChangeTarget cancel target ["+value+"] at an iteration "+i);
				return;
			}
		}

		logger.info("onChangeTarget add result ["+value+"]");
		results.add( value );
	}

	//----------------------------------------------
	// UNSUPPORTED OPERATION CASE
	//----------------------------------------------
	boolean check(){
		try {
			isHeadCall();
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean untracked_method_check(){
		return check();
	}

	final AtomicBoolean on_create_result = new AtomicBoolean( false );
	final AtomicBoolean on_timeout_result = new AtomicBoolean( false );

	@OnCreate
	void onCreate(){
		on_create_result.set( check() );
		timeout( "timeout_unsupported_operation", 0 );
	}

	@OnTime
	void timeout_unsupported_operation(){
		on_timeout_result.set( check() );
	}

	@Callable
	boolean on_create_method_check(){
		return on_create_result.get();
	}

	@Callable
	boolean timeout_method_check(){
		return on_timeout_result.get();
	}

	//----------------------------------------------
	// TIMING CASE
	//----------------------------------------------
	final AtomicLong timing_count = new AtomicLong( 0 );
	final AtomicLong timing_result = new AtomicLong( -1 );

	@OnCreate
	void onCreateTiming(){
		timeout( "a", 100, 0 );
		timeout( "a", 200, 1 );
	}

	@OnTime
	@Tracked
	void a( int value ){
		if( isHeadCall() ) {
			logger.info("onTime `a` start ["+value+"]");
			Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
			logger.info("onTime `a` check ["+value+"]");
			if( isHeadCall() ){
				logger.info("onTime `a` accept ["+value+"]");
				timing_count.incrementAndGet();
				timing_result.set( value );
			} else {
				logger.info("onTime `a` cancel ["+value+"]");
			}
		} else {
			logger.info("onTime `a` not start ["+value+"]");
		}
	}

	@Callable
	boolean timing_check(){
		return timing_count.get() == 1 && timing_result.get() == 1;
	}


	//----------------------------------------------
	// THROTTLED & TRACKED
	//----------------------------------------------
	@Autowired
	SLong throttled_target;

	@Autowired
	SList<Long> throttled_results;

	@OnChange("throttled_target") @Unlocked @Throttled(interval=300) @Tracked
	void onChangeThrottledTarget( final Long value ){
		logger.info("onChangeThrottledTarget start target ["+value+"]");
		for( int i=0; i<20; ++i ) {
			Uninterruptibles.sleepUninterruptibly(50, TimeUnit.MILLISECONDS);
			if( ! isHeadCall() ) {
				logger.info("onChangeThrottledTarget cancel target ["+value+"] at an iteration "+i);
				return;
			}
		}

		logger.info("onChangeThrottledTarget add result ["+value+"]");
		throttled_results.add( value );
	}
}
