/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import com.google.common.base.Objects;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.ExceptionHandler;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SLong;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class BasicsException2Controller {

	final Method init1Method;
	final Method init2Method;

	public BasicsException2Controller(){
		init1Method = ReflectionUtils.findMethod( this.getClass(), "init1" );
		init2Method = ReflectionUtils.findMethod( this.getClass(), "init2" );
	}

	@OnCreate
	void init1() {
		throw new NullPointerException();
	}

	@OnCreate
	void init2() {
		throw new RuntimeException();
	}

	@Autowired
	ControllerFacade facade;

	@Autowired @NonNull
	SLong handle1_result;

	@Autowired @NonNull
	SLong handle2_result;

	@Autowired @NonNull
	SLong handle3_result;

	@Autowired @NonNull
	SLong handle4_result;

	@ExceptionHandler @Unlocked
	void handle1( final NullPointerException e, final Method method ) {
		if( facade.isLockedByCurrentThread() != true && Objects.equal( method, init1Method ) ) {
			handle1_result.incrementAndGet();
		}
	}

	@ExceptionHandler @Unlocked
	void handle2( final Exception e ) {
		if( facade.isLockedByCurrentThread() != true ) {
			handle2_result.incrementAndGet();
		}
	}

	@ExceptionHandler @Unlocked
	void handle3( final Method method ) {
		if( facade.isLockedByCurrentThread() != true ) {
			handle3_result.incrementAndGet();
		}
	}

	@ExceptionHandler @Unlocked
	void handle4( final Method method, final RuntimeException e ) {
		if( facade.isLockedByCurrentThread() != true && Objects.equal( method, init2Method ) ) {
			handle4_result.incrementAndGet();
		}
	}
}
