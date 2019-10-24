/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.wcardinal.util.reflection.MethodHook;

public class TaskMethodHook implements MethodHook {
	final Controller controller;
	final TaskInternal task;

	TaskMethodHook( final Controller controller, final TaskInternal task ){
		this.controller = controller;
		this.task = task;
	}

	@Override
	public void before() {
		this.controller.task.set( task );
	}

	@Override
	public void after() {
		this.controller.task.set( null );
	}
}
