/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.exception;

/**
 * Thrown to indicate that a method has been passed a null argument which is not supported.
 */
public class NullArgumentException extends IllegalArgumentException {
	private static final long serialVersionUID = 4454618571379864803L;

	public NullArgumentException(){
		super();
	}

	public NullArgumentException( final String message ){
		super( message );
	}
}
