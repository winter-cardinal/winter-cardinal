/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.taskexceptionhandler.fail1;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.TaskExceptionHandler;

@Controller
public class SyntaxCheckController {
	@TaskExceptionHandler( "foo" )
	void handler(){}
}
