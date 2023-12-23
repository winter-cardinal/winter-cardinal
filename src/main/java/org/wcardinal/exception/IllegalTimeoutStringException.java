/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.exception;

import java.lang.reflect.Method;

public class IllegalTimeoutStringException extends RuntimeException {
	public IllegalTimeoutStringException( final String string, final Method method) {
		this( string, method, null );
	}

	public IllegalTimeoutStringException( final String string, final Method method, final Exception e ) {
		super( String.format("Illegal timeout string '%s' of '%s'", string, method.toGenericString()), e );
	}
}
