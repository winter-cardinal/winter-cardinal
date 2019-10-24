/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onidlecheck.success;

import org.wcardinal.controller.ControllerIo;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnIdleCheck;

@Controller
public class SyntaxIdleCheckController {
	@OnIdleCheck
	long onCreateA1(){
		return 0;
	}

	@OnIdleCheck
	long onCreateA2( final ControllerIo io ) {
		return 0;
	}
}
