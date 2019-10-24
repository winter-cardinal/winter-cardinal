/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.oncreate.fail1;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.Unlocked;

@Controller
public class SyntaxCheckController {
	@OnCreate
	@Locked
	@Unlocked
	void onCreate(){}
}
