/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.SortedMap;

import org.springframework.core.ResolvableType;

import org.wcardinal.controller.data.SNavigableMap;

public class MethodTypeCheckerFactoryForSNavigableMap implements MethodTypeCheckerFactory {
	final static MethodTypeCheckerFactoryForSNavigableMap INSTANCE = new MethodTypeCheckerFactoryForSNavigableMap();

	@Override
	public MethodTypeChecker create( final ResolvableType[] generics ) {
		return new MethodTypeCheckerThreeStatesForContainerType( SortedMap.class, String.class, generics[ 0 ], SNavigableMap.Update.class );
	}
}
