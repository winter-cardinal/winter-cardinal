/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.exception;

public class IllegalConstantException extends RuntimeException {
	private static final long serialVersionUID = -6608596766328445376L;
	public IllegalConstantException( final String message ) {
		super( message );
	}
}
