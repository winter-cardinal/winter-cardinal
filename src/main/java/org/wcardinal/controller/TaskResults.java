/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

public class TaskResults {
	private TaskResults(){}

	public static <T> TaskResult<T> success() {
		return create();
	}

	public static <T> TaskResult<T> success( final T result ) {
		return create( result );
	}

	public static <T> TaskResult<T> success( final Runnable runnable ) {
		return create( runnable );
	}

	public static <T> TaskResult<T> success( final T result, final Runnable runnable ) {
		return create( result, runnable );
	}

	public static <T> TaskResult<T> fail() {
		return new TaskResultFailed<T>();
	}

	public static <T> TaskResult<T> fail( final String reason ) {
		return new TaskResultFailed<T>( reason );
	}

	public static <T> TaskResult<T> create() {
		return create( null, null );
	}

	public static <T> TaskResult<T> create( final T result ) {
		return create( result, null );
	}

	public static <T> TaskResult<T> create( final Runnable runnable ) {
		return create( null, runnable );
	}

	public static <T> TaskResult<T> create( final T result, final Runnable runnable ) {
		return new TaskResult<T>( result, runnable );
	}
}
