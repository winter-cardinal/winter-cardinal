/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onshow.fail3;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnShow;

@Controller
public class SyntaxCheckController {
	@OnShow
	void onShowB3( final boolean parameter1, final String parameter2 ){}
}
