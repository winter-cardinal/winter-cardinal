/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.oncreate.fail4;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.Throttled;

@Controller
public class SyntaxCheckController {
	@OnCreate
	@Throttled(interval=1000)
	void onCreate(){}
}
