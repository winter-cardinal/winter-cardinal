/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.exceptionhandler.success;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.ExceptionHandler;
import org.wcardinal.controller.annotation.Task;
import org.wcardinal.exception.IllegalModifierException;

@Controller
public class SyntaxCheckController {
	@Task
	void foo() {}

	@ExceptionHandler( "foo" )
	String handler( final RuntimeException e ){
		return null;
	}

	@ExceptionHandler( "foo" )
	void handler( final Exception e ){}

	@ExceptionHandler( "foo" )
	void handler( final IllegalModifierException e ){}

	@ExceptionHandler( "foo" )
	void handler(){}

	@ExceptionHandler
	Exception handlerReturnType(){
		return null;
	}
}
