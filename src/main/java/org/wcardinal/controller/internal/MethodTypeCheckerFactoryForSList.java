/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.SortedMap;

import org.springframework.core.ResolvableType;

import org.wcardinal.controller.data.SList;

public class MethodTypeCheckerFactoryForSList implements MethodTypeCheckerFactory {
	final static MethodTypeCheckerFactoryForSList INSTANCE = new MethodTypeCheckerFactoryForSList();

	@Override
	public MethodTypeChecker create( final ResolvableType[] generics ) {
		return new MethodTypeCheckerThreeStatesForContainerType( SortedMap.class, Integer.class, generics[ 0 ], SList.Update.class );
	}
}
