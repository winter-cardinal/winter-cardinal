/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;

@Controller
public class BasicsInitializedController {
	@Autowired
	BasicsInitializedComponent initialized;

	@Autowired
	BasicsInitializedComponentNonNull nonnull;

	@Autowired
	BasicsInitializedComponentUninitialized uninitialized;
}
