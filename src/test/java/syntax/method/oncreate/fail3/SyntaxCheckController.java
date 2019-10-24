/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.oncreate.fail3;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Debounced;
import org.wcardinal.controller.annotation.OnCreate;

@Controller
public class SyntaxCheckController {
	@OnCreate
	@Debounced(interval=1000)
	void onCreate(){}
}
