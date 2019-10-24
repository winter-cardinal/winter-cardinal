/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SClass;

@Controller
public class BasicsBinaryController {
	@Autowired
	SClass<byte[]> bytes;

	@OnCreate
	void init(){
		bytes.set( new byte[]{1,1} );
	}
}
