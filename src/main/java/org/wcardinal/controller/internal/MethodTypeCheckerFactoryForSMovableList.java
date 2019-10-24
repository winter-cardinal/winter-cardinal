/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.springframework.core.ResolvableType;

public class MethodTypeCheckerFactoryForSMovableList implements MethodTypeCheckerFactory {
	final static MethodTypeCheckerFactoryForSMovableList INSTANCE = new MethodTypeCheckerFactoryForSMovableList();

	@Override
	public MethodTypeChecker create( final ResolvableType[] generics ) {
		return new MethodTypeCheckerForSMovableList( generics[ 0 ] );
	}
}
