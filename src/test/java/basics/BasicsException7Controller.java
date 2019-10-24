/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.ExceptionHandler;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SLong;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class BasicsException7Controller {

	final Method taskMethod;

	public BasicsException7Controller(){
		taskMethod = ReflectionUtils.findMethod( this.getClass(), "task" );
	}

	@OnCreate @Unlocked
	void init() {
		throw new NullPointerException();
	}

	@Autowired
	ControllerFacade facade;

	@Autowired @NonNull
	SLong handle1_result;

	@Autowired @NonNull
	SLong handle2_result;

	@Autowired @NonNull
	SLong handle3_result;

	@ExceptionHandler @Unlocked
	void handle1( final Method method, final NullPointerException e ) {
		if( facade.isLockedByCurrentThread() != true ) {
			handle1_result.incrementAndGet();
		}
	}

	@ExceptionHandler
	void handle2( final Exception e ) {
		handle2_result.incrementAndGet();
	}

	@ExceptionHandler
	void handle3( final Method method ) {
		handle3_result.incrementAndGet();
	}
}
