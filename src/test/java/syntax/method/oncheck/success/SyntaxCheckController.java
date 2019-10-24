/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.oncheck.success;

import javax.servlet.http.HttpServletRequest;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCheck;

@Controller
public class SyntaxCheckController {
	@OnCheck
	static boolean onCheckA1(){ return true; }

	@OnCheck
	static boolean onCheckA2( final HttpServletRequest parameter ){ return true; }

	@OnCheck
	static boolean onCheckA3( final Object parameter ){ return true; }
}
