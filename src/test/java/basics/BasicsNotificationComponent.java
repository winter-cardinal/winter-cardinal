/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ComponentFacade;
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.OnCreate;

@Component
public class BasicsNotificationComponent{
	@Autowired
	ComponentFacade facade;

	@OnCreate
	void onCreate(){
		facade.notifyAsync("bar", 1);
	}
}
