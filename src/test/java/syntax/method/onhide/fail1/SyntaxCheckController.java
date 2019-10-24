/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onhide.fail1;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnHide;

@Controller
public class SyntaxCheckController {
	@OnHide
	void onHideB1( final boolean parameter ){}
}
