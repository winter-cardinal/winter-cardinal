/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodResultException<T> implements MethodResult<T> {
	private final Method method;
	private final Exception exception;
	private final Object[] parameters;

	public MethodResultException( final Method method, final Object[] parameters, final Exception exception ){
		this.method = method;
		this.parameters = parameters;
		this.exception = exception;
	}

	public Exception getException() {
		return exception;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public Throwable getInvocationTargetThrowable(){
		if( exception instanceof InvocationTargetException ) {
			return exception.getCause();
		}
		return null;
	}
}
