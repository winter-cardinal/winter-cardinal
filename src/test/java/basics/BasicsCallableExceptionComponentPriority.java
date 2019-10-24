/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ComponentFacade;
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.CallableExceptionHandler;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.annotation.NonNull;

@Component
public class BasicsCallableExceptionComponentPriority {
	@Autowired
	ComponentFacade facade;

	@Callable
	void callable0(){
		count.set( 0 );
		null_pointer_exception_count.set( 0 );
		throw new NullPointerException();
	}

	@Callable
	void callable1(){
		count.set( 0 );
		null_pointer_exception_count.set( 0 );
		throw new RuntimeException();
	}

	@Callable
	void callable2(){
		count.set( 0 );
		null_pointer_exception_count.set( 0 );
		throw new NullPointerException();
	}

	@Autowired
	@NonNull
	SInteger count;

	@Autowired
	@NonNull
	SInteger null_pointer_exception_count;

	@CallableExceptionHandler
	String handle(){
		count.incrementAndGet();
		if( facade.isLockedByCurrentThread() ) count.incrementAndGet();
		return "d";
	}

	@CallableExceptionHandler({ "callable1", "callable2" })
	String handle( final NullPointerException e ){
		null_pointer_exception_count.incrementAndGet();
		if( facade.isLockedByCurrentThread() ) null_pointer_exception_count.incrementAndGet();
		return "e";
	}
}
