/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.taskexceptionhandler.fail2;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;
import org.wcardinal.controller.annotation.TaskExceptionHandler;

@Controller
public class SyntaxCheckController {
	@Task
	void foo() {}

	@TaskExceptionHandler( "foo" )
	void handler1( Exception e ){}

	@TaskExceptionHandler( "foo" )
	void handler2( Exception e ){}
}
