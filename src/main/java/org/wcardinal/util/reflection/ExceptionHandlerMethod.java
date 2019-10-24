/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

import org.springframework.core.ResolvableType;

public class ExceptionHandlerMethod<T> extends MethodWrapper<T> implements Comparable<ExceptionHandlerMethod<T>> {
	final int form;
	final ResolvableType type;
	int order;

	public ExceptionHandlerMethod( final Method method, final ResolvableType implementationType ){
		super( method );

		final Class<?>[] types = method.getParameterTypes();
		if( types.length == 0 ) {
			form = 0;
			type = null;
		} else if( types.length == 1 ) {
			final ResolvableType type0 = ResolvableType.forMethodParameter(MethodParameters.forMethod(method, 0), implementationType);
			final ResolvableType methodType = ResolvableType.forClass(Method.class);
			if( methodType.isAssignableFrom( type0 ) ) {
				form = 1;
				type = null;
			} else {
				form = 2;
				type = type0;
			}
		} else {
			final ResolvableType type0 = ResolvableType.forMethodParameter(MethodParameters.forMethod(method, 0), implementationType);
			final ResolvableType type1 = ResolvableType.forMethodParameter(MethodParameters.forMethod(method, 1), implementationType);
			final ResolvableType methodType = ResolvableType.forClass(Method.class);
			if( methodType.isAssignableFrom( type0 ) ) {
				form = 3;
				type = type1;
			} else {
				form = 4;
				type = type0;
			}
		}

		this.order = 0;
	}

	@Override
	public int compareTo( ExceptionHandlerMethod<T> other ) {
		return -Integer.compare(order, other.order);
	}

	public MethodResult<T> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Object instance, final Throwable throwable, final Method method ) {
		return super.call( container, trackingData, hook, instance, toParameters( throwable, method ) );
	}

	private Object[] toParameters( final Throwable throwable, final Method method ) {
		switch( form ) {
		case 0:
			return new Object[] {};
		case 1:
			return new Object[] { method };
		case 2:
			return new Object[] { throwable };
		case 3:
			return new Object[] { method, throwable };
		default:
			return new Object[] { throwable, method };
		}
	}
}
