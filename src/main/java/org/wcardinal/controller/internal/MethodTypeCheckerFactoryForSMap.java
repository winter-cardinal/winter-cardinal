/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.Map;

import org.springframework.core.ResolvableType;

import org.wcardinal.controller.data.SMap;

public class MethodTypeCheckerFactoryForSMap implements MethodTypeCheckerFactory {
	final static MethodTypeCheckerFactoryForSMap INSTANCE = new MethodTypeCheckerFactoryForSMap();

	@Override
	public MethodTypeChecker create( final ResolvableType[] generics ) {
		return new MethodTypeCheckerThreeStatesForContainerType( Map.class, String.class, generics[ 0 ], SMap.Update.class );
	}
}
