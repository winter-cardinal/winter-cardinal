/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.taskexceptionhandler.success;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;
import org.wcardinal.controller.annotation.TaskExceptionHandler;
import org.wcardinal.exception.IllegalModifierException;

@Controller
public class SyntaxCheckController {
	@Task
	void foo() {}

	@TaskExceptionHandler( "foo" )
	String handler( final RuntimeException e ){
		return null;
	}

	@TaskExceptionHandler( "foo" )
	void handler( final Exception e ){}

	@TaskExceptionHandler( "foo" )
	void handler( final IllegalModifierException e ){}

	@TaskExceptionHandler( "foo" )
	void handler(){}
}
