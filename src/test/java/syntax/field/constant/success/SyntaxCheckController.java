/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.field.constant.success;

import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.Controller;

enum ENUM_CONSTANT {
	ENUM_CONSTANT_1,
	ENUM_CONSTANT_2
}

@Controller
@Constant( ENUM_CONSTANT.class )
public class SyntaxCheckController {
}
