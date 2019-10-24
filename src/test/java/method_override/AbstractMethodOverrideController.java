/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package method_override;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SLong;
import org.wcardinal.controller.data.annotation.NonNull;

public abstract class AbstractMethodOverrideController extends AbstractController {
	@Autowired @NonNull
	SLong counter;

	@Autowired @NonNull
	SLong overload_counter;

	@Autowired @NonNull
	SLong override_counter;

	@OnCreate
	void onCreate() {
		timeout( "onTime", 0 );
		timeout( "onTimeOverload", 0 );
		timeout( "onTimeOverride", 0 );
	}

	@OnTime
	abstract void onTime();

	@OnTime
	void onTimeOverload() {
		overload_counter.incrementAndGet();
	}

	@OnTime
	void onTimeOverride() {
		override_counter.incrementAndGet();
	}
}
