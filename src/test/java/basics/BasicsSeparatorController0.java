/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;

@Controller(separators={"--DEFAULT1--", "--DEFAULT2--"})
public class BasicsSeparatorController0 {
	@Autowired
	BasicsSeparatorPage page;
}
