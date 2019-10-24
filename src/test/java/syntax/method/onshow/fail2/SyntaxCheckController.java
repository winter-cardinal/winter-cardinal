/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onshow.fail2;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnShow;

@Controller
public class SyntaxCheckController {
	@OnShow
	void onShowB2( final String parameter1, final boolean parameter2 ){}
}
