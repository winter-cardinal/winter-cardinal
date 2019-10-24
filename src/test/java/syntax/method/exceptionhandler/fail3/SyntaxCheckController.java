/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.exceptionhandler.fail3;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.ExceptionHandler;
import org.wcardinal.controller.annotation.Task;

@Controller
public class SyntaxCheckController {
	@Task
	void foo() {}

	@ExceptionHandler( "foo" )
	void handler1(){}

	@ExceptionHandler( "foo" )
	void handler2(){}
}
