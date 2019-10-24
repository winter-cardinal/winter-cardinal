/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package encoding.none;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SString;

@Controller
public class EncodingController {
	@Autowired
	SString field;

	@OnCreate
	void init(){
		field.set( "john" );
	}
}
