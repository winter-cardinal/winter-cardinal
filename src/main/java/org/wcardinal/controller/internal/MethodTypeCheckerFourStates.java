/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Method;

import org.springframework.core.ResolvableType;

import org.wcardinal.exception.IllegalArgumentTypeException;

public class MethodTypeCheckerFourStates extends MethodTypeCheckerForOnChange {
	public MethodTypeCheckerFourStates( final ResolvableType typeA, final ResolvableType typeB, final ResolvableType typeC ){
		super( typeA, typeA, typeB, typeC, typeC );
	}

	@Override
	void throwException( final Method method ){
		throw new IllegalArgumentTypeException( String.format( "Expecting (A, A, B, C, C) or its variants as parameter list of '%s' where A = '%s', B = '%s' and C = '%s'", method.toGenericString(), types[0], types[2], types[3] ) );
	}
}
