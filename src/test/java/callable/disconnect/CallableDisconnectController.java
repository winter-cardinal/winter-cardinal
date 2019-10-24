/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package callable.disconnect;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class CallableDisconnectController {
	@Callable
	void test() {}
}
