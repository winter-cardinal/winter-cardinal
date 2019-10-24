/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.List;
import java.util.SortedMap;

import org.springframework.core.ResolvableType;

import org.wcardinal.controller.data.SMovableList;

public class MethodTypeCheckerForSMovableList extends MethodTypeCheckerFourStates {
	public MethodTypeCheckerForSMovableList( final ResolvableType type ){
		super(
			ResolvableType.forClassWithGenerics(SortedMap.class, ResolvableType.forClass(Integer.class), type),
			ResolvableType.forClassWithGenerics(SortedMap.class, ResolvableType.forClass(Integer.class), ResolvableType.forClassWithGenerics(SMovableList.Update.class, type)),
			ResolvableType.forClassWithGenerics(List.class, ResolvableType.forClassWithGenerics(SMovableList.Move.class, type))
		);
	}
}
