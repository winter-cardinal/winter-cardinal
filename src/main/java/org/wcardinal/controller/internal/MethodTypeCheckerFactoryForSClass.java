/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.springframework.core.ResolvableType;

public class MethodTypeCheckerFactoryForSClass implements MethodTypeCheckerFactory {
	final static MethodTypeCheckerFactoryForSClass INSTANCE = new MethodTypeCheckerFactoryForSClass();

	@Override
	public MethodTypeChecker create( final ResolvableType[] generics ) {
		return new MethodTypeCheckerTwoStates( generics[ 0 ] );
	}
}
