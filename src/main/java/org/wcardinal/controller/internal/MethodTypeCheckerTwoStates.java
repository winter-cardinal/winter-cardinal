/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Method;

import org.springframework.core.ResolvableType;

import org.wcardinal.exception.IllegalArgumentTypeException;

public class MethodTypeCheckerTwoStates extends MethodTypeCheckerForOnChange {
	public MethodTypeCheckerTwoStates( final Class<?> type ){
		this( ResolvableType.forClass(type) );
	}

	public MethodTypeCheckerTwoStates( final ResolvableType type ){
		super( type, type );
	}

	@Override
	void throwException( final Method method ) {
		throw new IllegalArgumentTypeException( String.format( "Expecting (A, A) or its variants as parameter list of '%s' where A = '%s'", method.toGenericString(), types[0] ) );
	}
}
