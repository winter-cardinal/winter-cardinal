/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

public class TaskResultType {
	private TaskResultType(){}

	public static final String SUCCEEDED = "succeeded";

	public static final String NONE = "none";
	public static final String CANCELED = "canceled";
	public static final String NO_SUCH_TASK = "no-such-task";
	public static final String EXCEPTION = "exception";
	public static final String NULL_RESULT = "null-result";
}
