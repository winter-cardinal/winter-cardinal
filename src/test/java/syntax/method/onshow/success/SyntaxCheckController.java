/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onshow.success;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnShow;

@Controller
public class SyntaxCheckController {
	@OnShow
	void onShowA1(){}

	@OnShow
	void onShowA2( final String parameter ){}

	@OnShow
	void onShowA3( final Object parameter ){}
}
