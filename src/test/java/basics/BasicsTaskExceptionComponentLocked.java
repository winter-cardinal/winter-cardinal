/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ComponentFacade;
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.TaskExceptionHandler;
import org.wcardinal.controller.annotation.Task;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.annotation.NonNull;

@Component
public class BasicsTaskExceptionComponentLocked {
	@Autowired
	ComponentFacade facade;

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
	SInteger null_pointer_exception_count;

	@TaskExceptionHandler
	@Locked
	String handle(){
		count.incrementAndGet();
		if( facade.isLockedByCurrentThread() ) count.incrementAndGet();
		return "d";
	}

	@TaskExceptionHandler({ "task1", "task2" })
	@Locked
	String handle( final NullPointerException e ){
		null_pointer_exception_count.incrementAndGet();
		if( facade.isLockedByCurrentThread() ) null_pointer_exception_count.incrementAndGet();
		return "e";
	}
}
