/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onhide.fail2;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnHide;

@Controller
public class SyntaxCheckController {
	@OnHide
	void onHideB2( final String parameter1, final boolean parameter2 ){}
}
