/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import org.wcardinal.controller.internal.TaskResultType;

public class TaskResultFailed<T> extends TaskResult<T> {
	final String reason;

	public TaskResultFailed() {
		super( null, null );
		reason = TaskResultType.NONE;
	}

	public TaskResultFailed( final String reason ) {
		super( null, null );
		this.reason = reason;
	}

	public String getReason(){
		return this.reason;
	}
}
