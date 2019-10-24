/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onidlecheck.fail4;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnIdleCheck;

@Controller
public class SyntaxIdleCheckController {
	@OnIdleCheck
	long onIdleCheck( String name ){ return 0; }
}
