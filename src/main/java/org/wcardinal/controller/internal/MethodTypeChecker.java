/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Method;

import org.springframework.core.ResolvableType;

public interface MethodTypeChecker {
	boolean check( final Method method, final ResolvableType implementationType );
}
