/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package shared.polling;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SInteger;

@Controller
public class SharedController2 extends AbstractController {
	@Autowired
	SInteger field_integer;

	@OnCreate
	public void init(){
		field_integer.set(2);
	}

	@Callable
	public int add(int i){
		return field_integer.addAndGet(i);
	}
}
