/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.List;

import org.springframework.core.ResolvableType;

public class MethodTypeCheckerFactoryForSQueue implements MethodTypeCheckerFactory {
	final static MethodTypeCheckerFactoryForSQueue INSTANCE = new MethodTypeCheckerFactoryForSQueue();

	@Override
	public MethodTypeChecker create( final ResolvableType[] generics ) {
		return new MethodTypeCheckerTwoStatesForContainerType( List.class, generics[ 0 ] );
	}
}
