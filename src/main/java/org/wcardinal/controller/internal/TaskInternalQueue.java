/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.ArrayDeque;

public class TaskInternalQueue {
	final ArrayDeque<TaskInternal> tasks;

	public TaskInternalQueue(){
		this.tasks = new ArrayDeque<>();
	}

	public void add( TaskInternal task ){
		tasks.add( task );
	}

	public TaskInternal poll(){
		return tasks.poll();
	}

	public TaskInternal peek(){
		return tasks.peek();
	}
}
