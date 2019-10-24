/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.task.success;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.ReadOnly;
import org.wcardinal.controller.annotation.Task;
import org.wcardinal.controller.data.annotation.Historical;
import org.wcardinal.controller.data.annotation.NonNull;

abstract class SyntaxCheckControllerSuper<T> {
	@Task
	void taskOverride() {}

	@Task
	abstract void taskAbstract();

	@Task
	void taskGeneric( T arg ) {}
}

@Controller
public class SyntaxCheckController extends SyntaxCheckControllerSuper<String> {
	@Task
	void task1(){}

	@Task
	static void task2(){}

	@Task @ReadOnly
	void task3(){}

	@Task @ReadOnly
	static void task4(){}

	@Task @NonNull
	void task5(){}

	@Task @NonNull
	static void task6(){}

	@Task @Historical
	void task7(){}

	@Task @Historical
	static void task8(){}

	@Override
	void taskOverride() {}

	@Override
	void taskAbstract() {}

	@Task
	@Override
	void taskGeneric( String arg ) {}
}
