/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.task.fail2;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

class SyntaxCheckControllerSuper<T> {
	@Task
	void task( final T name ){}
}

@Controller
public class SyntaxCheckController extends SyntaxCheckControllerSuper<String> {
	@Task
	void task(){}
}
