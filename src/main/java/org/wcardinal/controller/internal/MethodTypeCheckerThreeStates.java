/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Method;

import org.springframework.core.ResolvableType;

import org.wcardinal.exception.IllegalArgumentTypeException;

public class MethodTypeCheckerThreeStates extends MethodTypeCheckerForOnChange {
	public MethodTypeCheckerThreeStates( final ResolvableType typeA, final ResolvableType typeB ){
		super( typeA, typeA, typeB );
	}

	@Override
	void throwException( final Method method ){
		throw new IllegalArgumentTypeException( String.format( "Expecting (A, A, B) or its variants as parameter list of '%s' where A = '%s' and B = '%s'", method.toGenericString(), types[0], types[2] ) );
	}
}
