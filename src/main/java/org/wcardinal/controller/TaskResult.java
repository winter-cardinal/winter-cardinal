/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

/**
 * Represents a task result.
 *
 * @param <T> task result type
 */
public class TaskResult<T> {
	public final T result;
	public final Runnable runnable;

	public TaskResult( final T result, final Runnable runnable ) {
		this.result = result;
		this.runnable = runnable;
	}
}
