/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.factory;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class FactoryReadonlySyncController {
	@Autowired
	ComponentFactory<FactoryReadonlySyncComponent> factory;

	@Callable
	void create(){
		factory.create();
	}
}
