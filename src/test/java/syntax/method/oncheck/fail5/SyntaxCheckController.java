/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.oncheck.fail5;

import javax.servlet.http.HttpServletRequest;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCheck;

@Controller
public class SyntaxCheckController {
	@OnCheck
	static boolean onCheckC3( final String parameter1, final HttpServletRequest parameter2 ){ return true; }
}
