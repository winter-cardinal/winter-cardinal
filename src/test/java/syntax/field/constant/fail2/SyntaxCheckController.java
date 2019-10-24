/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.field.constant.fail2;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.PageFactory;
import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class SyntaxCheckController {
	@Autowired @Constant
	PageFactory<SyntaxCheckFactory> invalid_page_factory;
}
