/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SLong;

public class BasicsComponent {
	@Autowired
	SLong time;

	@OnCreate
	void onCreate(){
		this.time.set( System.currentTimeMillis() );
	}
}
