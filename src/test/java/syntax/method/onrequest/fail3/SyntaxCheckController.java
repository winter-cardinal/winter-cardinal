/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onrequest.fail3;

import javax.servlet.http.HttpServletRequest;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnRequest;

@Controller
public class SyntaxCheckController {
	@OnRequest
	static void onRequest( final HttpServletRequest parameter1, final String parameter2 ){}
}
