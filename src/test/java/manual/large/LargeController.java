/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.large;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.annotation.ReadOnly;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SLong;
import org.wcardinal.controller.data.SString;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class LargeController {
	@Autowired
	ControllerFacade facade;

	@Autowired
	@NonNull
	SLong id;

	@Autowired
	@ReadOnly
	SString data;

	@Autowired
	@NonNull
	SLong tick;

	@OnCreate
	void onCreate(){
		String value = String.valueOf(System.currentTimeMillis());
		for( int i=0; i<5000; ++i ){
			value = value + (Math.random()*System.currentTimeMillis());
		};

		facade.interval("update", 0, 10000, value);
		facade.interval("periodic", 0, 1000);
	}

	@OnTime
	@Locked
	void update( final String value ){
		data.set(value);
		id.incrementAndGet();
	}

	@OnTime
	@Unlocked
	void periodic(){
		tick.incrementAndGet();
	}
}
