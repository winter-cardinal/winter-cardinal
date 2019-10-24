/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.field.fail1;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.ReadOnly;
import org.wcardinal.controller.data.SString;
import org.wcardinal.controller.data.annotation.Historical;

@Controller
public class SyntaxCheckController {
	@ReadOnly @Historical
	SString field_string_readonly_historical;
}
