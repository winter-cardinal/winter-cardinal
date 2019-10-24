/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onrequest.fail1;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnRequest;

@Controller
public class SyntaxCheckController {
	@OnRequest
	void onRequest(){}
}
