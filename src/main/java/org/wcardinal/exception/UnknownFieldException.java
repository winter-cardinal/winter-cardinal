/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.exception;

public class UnknownFieldException extends RuntimeException {
	private static final long serialVersionUID = 5867196843419354448L;

	public UnknownFieldException( final String message ) {
		super( message );
	}
}
