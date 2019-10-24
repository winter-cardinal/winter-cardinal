/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.history;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Page;

@Page
public class ThirdPage {
	@Autowired
	PopupA popup_a;

	@Autowired
	PopupB popup_b;
}
