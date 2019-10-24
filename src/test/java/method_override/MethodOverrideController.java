/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package method_override;

import org.wcardinal.controller.annotation.Controller;

@Controller
public class MethodOverrideController extends AbstractMethodOverrideController {
	void onTime() {
		this.counter.incrementAndGet();
	}

	void onTimeOverload( int a ) {
		overload_counter.incrementAndGet();
	}

	void onTimeOverride() {
		override_counter.incrementAndGet();
	}
}
