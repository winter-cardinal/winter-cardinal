/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Component;

@Component
public class BasicsControllerScopeComponent {
	@Autowired
	BasicsControllerScopeBean bean;

	@Callable
	int get(){
		return bean.get();
	}
}
