/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package variable;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.Primary;
import org.wcardinal.controller.data.SString;

@Controller
public class VariableController {
	@Autowired
	PageController page_a;

	@Autowired
	@Primary
	PageController page_b;

	@Autowired
	SString field;

	@OnCreate
	void init(){
		field.set( "john" );
	}
}
