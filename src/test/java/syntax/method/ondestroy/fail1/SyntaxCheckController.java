/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.ondestroy.fail1;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnDestroy;

@Controller
public class SyntaxCheckController {
	@OnDestroy
	void onDestryB( final Object parameter ){}
}
