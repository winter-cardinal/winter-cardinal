/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package factory_create;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class FactoryCreateController {
	@Autowired
	ComponentFactory<FactoryCreateComponent> factory;
}
