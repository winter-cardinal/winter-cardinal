/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onhide.fail3;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnHide;

@Controller
public class SyntaxCheckController {
	@OnHide
	void onHideB3( final boolean parameter1, final String parameter2 ){}
}
