/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.history;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Component;

@Component
public class ComponentA {
	@Autowired
	FirstPage first;

	@Autowired
	SecondPage second;

	@Autowired
	ThirdPage third;
}
