/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.oncheck.fail3;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCheck;

@Controller
public class SyntaxCheckController {
	@OnCheck
	static boolean onCheckC1( final String parameter ){ return true; }
}
