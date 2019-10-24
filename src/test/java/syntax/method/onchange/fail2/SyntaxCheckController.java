/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onchange.fail2;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.data.SString;

@Controller
public class SyntaxCheckController {
	@Autowired
	SString bar;

	@Autowired
	SyntaxCheckComponent component;

	@OnChange( "component.foo" )
	void onChangeB2(){}
}
