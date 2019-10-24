/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package disconnect.polling;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.KeepAlive;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SLong;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller(keepAlive=@KeepAlive(ping=-1))
public class DisconnectController extends AbstractController {
	@Autowired @NonNull
	SLong tick;

	@OnCreate
	void init() {
		interval( "tick", 0, 500 );
	}

	@OnTime
	void tick() {
		tick.incrementAndGet();
	}

	@Callable
	long getTick() {
		final long result = tick.get();
		trigger("tick", result);
		return result;
	}
}
