/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.exception;

public class UnknownTargetException extends RuntimeException {
	private static final long serialVersionUID = -9193623096012114826L;
	public UnknownTargetException( final String message ) {
		super( message );
	}
}
