/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.OnCreate;

@Component
public class BasicsConstantComponent {
	//-----------------------------------------
	// STATIC CONSTANTS
	//-----------------------------------------
	@Constant
	static int INT_CONSTANT = 1;

	//-----------------------------------------
	// INSTANCE CONSTANTS
	//-----------------------------------------
	@Constant
	int INT_CONSTANT_INSTANCE;

	@OnCreate
	void onCreate(){
		INT_CONSTANT_INSTANCE = 2;
	}
}
