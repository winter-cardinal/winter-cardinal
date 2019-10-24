/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.exception;

import java.lang.reflect.Method;

import org.springframework.core.ResolvableType;

public class AmbiguousExceptionHandlerException extends RuntimeException {
	private static final long serialVersionUID = 7415069462208909448L;
	public AmbiguousExceptionHandlerException( final String message ) {
		super( message );
	}

	public AmbiguousExceptionHandlerException( final Method a, final Method b ) {
		super( String.format( "Exception handlers '%s' and '%s' are ambiguous because these two have no throwable in their arguments", a.toGenericString(), b.toGenericString() ) );
	}

	public AmbiguousExceptionHandlerException( final Method a, final Method b, final ResolvableType throwableClass ) {
		super( String.format( "Exception handlers '%s' and '%s' are ambiguous because these two have the same throwable '%s' in their arguments", a.toGenericString(), b.toGenericString(), throwableClass ) );
	}
}
