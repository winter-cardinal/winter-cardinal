/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.exceptionhandler.fail1;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.ExceptionHandler;

@Controller
public class SyntaxCheckController {
	@ExceptionHandler( "foo" )
	void handler(){}
}
