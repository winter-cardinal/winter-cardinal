/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SBoolean;

@Controller
public class BasicsLockController {
	@Autowired
	ControllerFacade facade;

	@Autowired
	SBoolean on_create_a_is_locked;

	@Autowired
	SBoolean on_create_b_is_locked;

	@Autowired
	SBoolean on_create_c_is_locked;

	@OnCreate
	void onCreateA(){
		on_create_a_is_locked.set( facade.isLockedByCurrentThread() );
	}

	@OnCreate
	@Unlocked
	void onCreateB(){
		this.on_create_b_is_locked.set( facade.isLockedByCurrentThread() );
	}

	@OnCreate
	@Locked
	void onCreateC(){
		this.on_create_c_is_locked.set( facade.isLockedByCurrentThread() );
	}
}
