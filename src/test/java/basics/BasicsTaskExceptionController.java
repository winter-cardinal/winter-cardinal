/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.TaskExceptionHandler;
import org.wcardinal.controller.annotation.Task;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class BasicsTaskExceptionController {
	@Autowired
	ControllerFacade facade;

	@Task
	void task0(){
		throw new NullPointerException();
	}

	@Task
	void task1(){
		throw new RuntimeException();
	}

	@Task
	void task2(){
		throw new NullPointerException();
	}

	@Autowired
	@NonNull
	SInteger count;

	@Autowired
	@NonNull
	SInteger exception_count;

	@Autowired
	@NonNull
	SInteger null_pointer_exception_count;

	@TaskExceptionHandler
	void handle(){
		count.incrementAndGet();
		if( facade.isLockedByCurrentThread() ) count.incrementAndGet();
	}

	@TaskExceptionHandler
	String handle( final Exception e ){
		exception_count.incrementAndGet();
		if( facade.isLockedByCurrentThread() ) exception_count.incrementAndGet();
		return "a";
	}

	@TaskExceptionHandler({ "task1", "task2" })
	String handle( final NullPointerException e ){
		null_pointer_exception_count.incrementAndGet();
		if( facade.isLockedByCurrentThread() ) null_pointer_exception_count.incrementAndGet();
		return "b";
	}

	@Autowired
	BasicsTaskExceptionComponent component;

	@Autowired
	BasicsTaskExceptionComponentPriority priority;

	@Autowired
	BasicsTaskExceptionComponentNoReason no_reason;

	@Autowired
	BasicsTaskExceptionComponentLocked locked;

	@Autowired
	BasicsTaskExceptionComponentUnlocked unlocked;
}
