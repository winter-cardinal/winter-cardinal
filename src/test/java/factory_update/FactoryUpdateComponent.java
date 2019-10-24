/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package factory_update;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SROQueue;

public class FactoryUpdateComponent {
	@Autowired
	SROQueue<String> queue;

	@OnCreate
	void init(){
		queue.capacity( 1 );
		queue.add("@@@");
	}
}
