/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.message;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SString;

public class MessageFactoryComponent {
	@Autowired
	SString name;

	@OnCreate
	void init() {
		name.set( "Cardinal" );
	}
}
