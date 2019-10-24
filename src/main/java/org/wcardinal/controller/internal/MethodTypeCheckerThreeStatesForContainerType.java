/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.springframework.core.ResolvableType;

public class MethodTypeCheckerThreeStatesForContainerType extends MethodTypeCheckerThreeStates {
	public MethodTypeCheckerThreeStatesForContainerType( final Class<?> containerType, final Class<?> keyType, final ResolvableType valueType, final Class<?> updateType ){
		super(
			ResolvableType.forClassWithGenerics(containerType, ResolvableType.forClass(keyType), valueType),
			ResolvableType.forClassWithGenerics(containerType, ResolvableType.forClass(keyType), ResolvableType.forClassWithGenerics(updateType, valueType))
		);
	}
}
