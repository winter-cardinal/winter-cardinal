/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.generics;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;

@Controller
public class GenericsController {
	@Autowired
	GenericsComponent<List<String>, String> component;

	@Autowired
	GenericsPage<List<String>> page;

	@Autowired
	GenericsPopup<List<String>> popup;

	@Autowired
	GenericsComponentSubclass<List<String>> subclass;
}
