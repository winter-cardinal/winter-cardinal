/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.exception;

public class TaskOverloadException extends RuntimeException {
	private static final long serialVersionUID = 7131068257846300052L;

	public TaskOverloadException( final String message ) {
		super( message );
	}
}
