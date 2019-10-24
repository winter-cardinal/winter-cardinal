/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;

class MethodParameters {
	@SuppressWarnings( "deprecation" )
	static MethodParameter forMethod( Method method, int index ) {
		return MethodParameter.forMethodOrConstructor( method, index );
	}
}
