/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.field.constant.fail5;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class SyntaxCheckController {
	@Autowired @Constant
	SyntaxCheckPage invalid_page;
}
