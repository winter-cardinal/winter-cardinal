/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.TimeoutFuture;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SInteger;

@Controller
public class BasicsTimeoutController extends AbstractController {
	//-----------------------------------------
	// TIMEOUT
	//-----------------------------------------
	@Callable
	void start(){
		count.set(0);
		total.set(0);
		timeout("a", 0, 12);
	}

	@Callable
	void restart(){
		timeout("a", 0, 24);
	}

	@Autowired
	SInteger count;

	@Autowired
	SInteger total;

	@OnTime
	void a( final int number ){
		count.getAndIncrement();
		timeout("b", 0, number + 1);
	}

	@OnTime
	void b( final int number ){
		count.getAndIncrement();
		timeout("c", 0, number + 1);
	}

	@OnTime
	@Locked
	void c( final int number ){
		count.getAndIncrement();
		total.set(number);
	}

	//-----------------------------------------
	// TIMEOUT RUNNABLE
	//-----------------------------------------
	@Callable
	void start_runnable(){
		timeout( new Runnable(){
			@Override
			public void run() {
				count.incrementAndGet();
			}
		}, 0);
	}

	@Callable
	boolean start_runnable_cancel(){
		final long id = timeout(new Runnable(){
			@Override
			public void run(){

			}
		}, 5000);

		return cancel( id );
	}

	//-----------------------------------------
	// TIMEOUT CALLABLE
	//-----------------------------------------
	@Callable
	void start_callable(){
		final TimeoutFuture<String> future = timeout( new java.util.concurrent.Callable<String>(){
			@Override
			public String call() throws Exception {
				return "a";
			}
		}, 0);

		if( future.isCancelled() == true ) return;

		future.getId();

		if( 0 < future.getDelay(TimeUnit.MILLISECONDS) ) return;

		try {
			if( Objects.equal(future.get(), "a") != true ) return;
		} catch (final Exception e) {
			return;
		}

		if( future.isDone() != true ) return;
		count.incrementAndGet();
	}

	@Callable
	void start_callable_cancel(){
		final TimeoutFuture<String> future = timeout( new java.util.concurrent.Callable<String>(){
			@Override
			public String call() throws Exception {
				return "a";
			}
		}, 5000);

		if( future.isCancelled() == true ) return;
		if( future.cancel( true ) != true ) return;
		if( future.isCancelled() != true ) return;
		count.incrementAndGet();
	}

	@Callable
	void start_callable_timeout_1(){
		final TimeoutFuture<String> future = timeout( new java.util.concurrent.Callable<String>(){
			@Override
			public String call() throws Exception {
				return "a";
			}
		}, 5000);

		if( future.isCancelled() == true ) return;
		try {
			future.get( 100, TimeUnit.MILLISECONDS );
			return;
		} catch ( final Exception e ) {
			if( future.cancel(true) != true ) return;
		}
		if( future.isCancelled() != true ) return;
		count.incrementAndGet();
	}

	@Callable
	void start_callable_timeout_2(){
		final TimeoutFuture<String> future = timeout( new java.util.concurrent.Callable<String>(){
			@Override
			public String call() throws Exception {
				return "a";
			}
		}, 0);

		if( future.isCancelled() == true ) return;
		try {
			if( Objects.equal( future.get( 1000, TimeUnit.MILLISECONDS ), "a" ) != true ) return;
		} catch ( final Exception e ) {
			return;
		}
		count.incrementAndGet();
	}

	@Callable
	void start_callable_exception_1(){
		final TimeoutFuture<String> future = timeout( new java.util.concurrent.Callable<String>(){
			@Override
			public String call() throws Exception {
				throw new RuntimeException();
			}
		}, 0);

		try {
			future.get();
			return;
		} catch ( final Exception e ) {
			if( (e instanceof ExecutionException) != true ) return;
		}
		count.incrementAndGet();
	}

	@Callable
	void start_callable_exception_2(){
		final TimeoutFuture<String> future = timeout( new java.util.concurrent.Callable<String>(){
			@Override
			public String call() throws Exception {
				throw new RuntimeException();
			}
		}, 0);

		try {
			future.get( 100, TimeUnit.MILLISECONDS );
			return;
		} catch ( final Exception e ) {
			if( (e instanceof ExecutionException) != true ) return;
		}
		count.incrementAndGet();
	}
}
