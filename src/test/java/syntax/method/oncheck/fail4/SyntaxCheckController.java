/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.oncheck.fail4;

import javax.servlet.http.HttpServletRequest;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCheck;

@Controller
public class SyntaxCheckController {
	@OnCheck
	static boolean onCheckC2( final HttpServletRequest parameter1, final String parameter2 ){ return true; }
}
