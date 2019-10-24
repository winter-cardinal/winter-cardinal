/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package factory_create;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SList;

public class FactoryCreateComponent {
	@Autowired
	SList<String> field;

	@OnCreate
	void init( String value ){
		field.clear();
		field.addAll(Arrays.asList( value ));
	}
}
