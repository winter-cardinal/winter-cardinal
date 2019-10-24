/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.exception;

public class IllegalArgumentTypeException extends RuntimeException {
	private static final long serialVersionUID = -6608596766328445376L;
	public IllegalArgumentTypeException( final String message ) {
		super( message );
	}
}
