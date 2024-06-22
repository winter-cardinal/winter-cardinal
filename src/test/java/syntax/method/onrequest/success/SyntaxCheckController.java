/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onrequest.success;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import org.wcardinal.controller.ControllerAttributes;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnRequest;

@Controller
public class SyntaxCheckController {
	@OnRequest
	static void onRequestA1(){}

	@OnRequest
	static void onRequestA2( final Object parameter ){}

	@OnRequest
	static void onRequestA3( final Object parameter1, final Object parameter2 ){}

	@OnRequest
	static void onRequestA4( final ControllerAttributes parameter1, final Object parameter2 ){}

	@OnRequest
	static void onRequestA4( final ControllerAttributes parameter1, final HttpServletRequest parameter2 ){}

	@OnRequest
	static void onRequestA4( final ControllerAttributes parameter1, final ServletRequest parameter2 ){}
}
