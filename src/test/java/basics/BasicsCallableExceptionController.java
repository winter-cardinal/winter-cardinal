/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.CallableExceptionHandler;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class BasicsCallableExceptionController {
	@Autowired
	ControllerFacade facade;

	@Callable
	void callable0(){
		count.set( 0 );
		exception_count.set( 0 );
		null_pointer_exception_count.set( 0 );
		throw new NullPointerException();
	}

	@Callable
	void callable1(){
		count.set( 0 );
		exception_count.set( 0 );
		null_pointer_exception_count.set( 0 );
		throw new RuntimeException();
	}

	@Callable
	void callable2(){
		count.set( 0 );
		exception_count.set( 0 );
		null_pointer_exception_count.set( 0 );
		throw new NullPointerException();
	}

	@Autowired
	SInteger count;

	@Autowired
	@NonNull
	SInteger exception_count;

	@Autowired
	@NonNull
	SInteger null_pointer_exception_count;

	@CallableExceptionHandler
	void handle(){
		count.incrementAndGet();
		if( facade.isLockedByCurrentThread() ) count.incrementAndGet();
	}

	@CallableExceptionHandler
	String handle( final Exception e ){
		exception_count.incrementAndGet();
		if( facade.isLockedByCurrentThread() ) exception_count.incrementAndGet();
		return "a";
	}

	@CallableExceptionHandler({ "callable1", "callable2" })
	String handle( final NullPointerException e ){
		null_pointer_exception_count.incrementAndGet();
		if( facade.isLockedByCurrentThread() ) null_pointer_exception_count.incrementAndGet();
		return "b";
	}

	@Autowired
	BasicsCallableExceptionComponent component;

	@Autowired
	BasicsCallableExceptionComponentPriority priority;

	@Autowired
	BasicsCallableExceptionComponentNoReason no_reason;

	@Autowired
	BasicsCallableExceptionComponentLocked locked;

	@Autowired
	BasicsCallableExceptionComponentUnlocked unlocked;
}
