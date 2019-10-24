/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.oncreate.success;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;

@Controller
public class SyntaxCheckController {
	@OnCreate
	void onCreateA1(){}

	@OnCreate
	void onCreateA2( final Object parameter ){}
}
