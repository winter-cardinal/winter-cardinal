/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;

import org.wcardinal.exception.IllegalReturnTypeException;

public class MethodTypeCheckerForTaskExceptionHandler extends MethodTypeCheckerForExceptionHandler {
	static final Logger logger = LoggerFactory.getLogger(MethodTypeCheckerForTaskExceptionHandler.class);
	public static MethodTypeChecker INSTANCE = new MethodTypeCheckerForTaskExceptionHandler();

	MethodTypeCheckerForTaskExceptionHandler(){}

	@Override
	public boolean check( final Method method, final ResolvableType implementationType ) {
		// Return type check
		final Class<?> returnType = method.getReturnType();
		if( String.class != returnType && void.class != returnType ) {
			throw new IllegalReturnTypeException( String.format( "Exception handler '%s' must have a return type of String or void", method.toGenericString() ) );
		}

		return super.check( method,  implementationType );
	}
}
