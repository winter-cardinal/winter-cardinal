/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.trigger_direct_performance;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnTime;

@Controller
public class TriggerPerformanceController {
	@Autowired
	ControllerFacade facade;

	@Callable
	void start( final int interval ) {
		facade.interval( "trigger", interval );
	}

	final AtomicLong counter = new AtomicLong( 0 );

	@OnTime
	void trigger() {
		facade.triggerDirect("event", counter.getAndIncrement());
	}
}
