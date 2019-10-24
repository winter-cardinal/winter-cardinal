/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Method;

import org.springframework.core.ResolvableType;

public class MethodTypeCheckerEmpty implements MethodTypeChecker {
	public static MethodTypeChecker INSTANCE = new MethodTypeCheckerEmpty();

	MethodTypeCheckerEmpty(){ }

	@Override
	public boolean check( final Method method, final ResolvableType implementationType ) {
		return true;
	}
}
