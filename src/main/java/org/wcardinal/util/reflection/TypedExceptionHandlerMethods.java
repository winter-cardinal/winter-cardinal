/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import org.springframework.core.ResolvableType;

public class TypedExceptionHandlerMethods<T> extends AbstractTypedMethods<ExceptionHandlerMethods<T>, ExceptionHandlerMethod<T>, T> {
	public ExceptionHandlerMethod<T> find( final String type, final Class<? extends Throwable> throwableClass ){
		final ExceptionHandlerMethods<T> wrappers = typeToMethods.get( type );
		if( wrappers != null ){
			final ExceptionHandlerMethod<T> result = wrappers.find( throwableClass );
			if( result != null ) return result;
		}

		return defaults.find( throwableClass );
	}

	@Override
	ExceptionHandlerMethods<T> create() {
		return new ExceptionHandlerMethods<T>();
	}

	public static <T> TypedExceptionHandlerMethods<T> create( final Set<Method> methods, final ResolvableType implementationType, final Class<? extends Annotation> annotationClass ){
		return create( methods, implementationType, annotationClass, false );
	}

	public static <T> TypedExceptionHandlerMethods<T> create( final Set<Method> methods, final ResolvableType implementationType, final Class<? extends Annotation> annotationClass, final boolean defaultsToMethodName ){
		final TypedExceptionHandlerMethods<T> result = new TypedExceptionHandlerMethods<T>();

		for( final Method method: methods ){
			result.add(annotationClass, new ExceptionHandlerMethod<T>( method, implementationType ), defaultsToMethodName);
		}

		return result;
	}
}
