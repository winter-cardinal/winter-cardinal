/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class BasicsFactoryParameterController {
	@Autowired
	PopupFactory<BasicsFactoryParameterPopup> factory;

	@Callable
	void create(){
		factory.create( System.currentTimeMillis() );
	}
}
