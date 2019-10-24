/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

import org.wcardinal.exception.IllegalArgumentTypeException;

public class MethodTypeCheckerForExceptionHandler implements MethodTypeChecker {
	static final Logger logger = LoggerFactory.getLogger(MethodTypeCheckerForExceptionHandler.class);
	public static MethodTypeChecker INSTANCE = new MethodTypeCheckerForExceptionHandler();

	MethodTypeCheckerForExceptionHandler(){}

	@Override
	public boolean check( final Method method, final ResolvableType implementationType ) {
		// Argument check
		final Class<?>[] types = method.getParameterTypes();
		if( types.length == 0 ) {
			return true;
		} else if( types.length == 1 ) {
			final ResolvableType type0 = ResolvableType.forMethodParameter(new MethodParameter(method, 0), implementationType);
			final ResolvableType throwableType = ResolvableType.forClass(Throwable.class);
			final ResolvableType methodType = ResolvableType.forClass(Method.class);
			if( throwableType.isAssignableFrom( type0 ) || methodType.isAssignableFrom( type0 ) ) {
				return true;
			}
		} else if( types.length == 2 ) {
			final ResolvableType type0 = ResolvableType.forMethodParameter(new MethodParameter(method, 0), implementationType);
			final ResolvableType type1 = ResolvableType.forMethodParameter(new MethodParameter(method, 1), implementationType);
			final ResolvableType throwableType = ResolvableType.forClass(Throwable.class);
			final ResolvableType methodType = ResolvableType.forClass(Method.class);
			if( throwableType.isAssignableFrom( type0 ) && methodType.isAssignableFrom( type1 ) ) {
				return true;
			} else if( throwableType.isAssignableFrom( type1 ) && methodType.isAssignableFrom( type0 ) ) {
				return true;
			}
		}
		throw new IllegalArgumentTypeException( String.format( "Expecting (A, B), (A), (B) or () as parameter list of '%s' where A = '%s' and B = '%s'", method.toGenericString(), Throwable.class, Method.class ) );
	}
}
