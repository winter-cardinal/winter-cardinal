/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.task.fail1;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
public class SyntaxCheckController {
	@Task
	void task(){}

	@Task
	void task( final String name ){}
}
