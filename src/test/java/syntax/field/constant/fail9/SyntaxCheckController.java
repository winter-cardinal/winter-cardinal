/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.field.constant.fail9;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.PageFactory;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class SyntaxCheckController {
	@Autowired
	PageFactory<SyntaxCheckFactory> invalid_page_factory;
}
