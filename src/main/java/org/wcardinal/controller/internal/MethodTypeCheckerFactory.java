/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.springframework.core.ResolvableType;

public interface MethodTypeCheckerFactory {
	MethodTypeChecker create( ResolvableType[] generics );
}
