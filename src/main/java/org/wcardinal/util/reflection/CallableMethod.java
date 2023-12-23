/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

public class CallableMethod<T> extends AbstractCallableMethod<T> {
	public CallableMethod( final Method method, final ResolvableType implementationType, final ApplicationContext context ){
		super( method, implementationType, context );
	}
}
