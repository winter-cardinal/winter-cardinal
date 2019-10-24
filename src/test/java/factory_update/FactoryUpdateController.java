/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package factory_update;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;

@Controller
public class FactoryUpdateController extends AbstractController {
	@Autowired
	ComponentFactory<FactoryUpdateComponent> factory;

	@OnCreate
	void create(){
		interval( "update", 1000 );
	}

	@OnTime
	void update() {
		factory.lock();
		try {
			factory.clear();
			factory.create();
		} finally {
			factory.unlock();
		}
	}
}
