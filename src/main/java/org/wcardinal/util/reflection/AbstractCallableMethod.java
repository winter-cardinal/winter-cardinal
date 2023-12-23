/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.jackson.databind.JavaType;

import org.wcardinal.controller.annotation.Ajax;
import org.wcardinal.controller.annotation.ReadOnly;
import org.wcardinal.controller.annotation.Task;
import org.wcardinal.controller.annotation.Timeout;
import org.wcardinal.controller.data.annotation.Historical;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.exception.IllegalTimeoutStringException;
import org.wcardinal.util.json.Json;

public class AbstractCallableMethod<T> extends MethodWrapper<T> {
	final long timeout;
	final boolean ajax;
	final JavaType[] types;
	final boolean task;
	final boolean historical;
	final boolean nonnull;
	final boolean readonly;

	public AbstractCallableMethod( final Method method, final ResolvableType implementationType, final ApplicationContext context ){
		super( method );

		// Timeout
		this.timeout = toTimeout( method, implementationType, context );

		// Ajax
		this.ajax = (AnnotationUtils.findAnnotation(this.method, Ajax.class) != null);

		// Types
		this.types = toTypes( method, implementationType );

		// Task
		this.task = (AnnotationUtils.findAnnotation(this.method, Task.class) != null);

		// Historical
		this.historical = (AnnotationUtils.findAnnotation(this.method, Historical.class) != null);

		// Non-null
		this.nonnull = (AnnotationUtils.findAnnotation(this.method, NonNull.class) != null);

		// Read-only
		this.readonly = (AnnotationUtils.findAnnotation(this.method, ReadOnly.class) != null);
	}

	long toTimeout( final Method method, final ResolvableType implementationType, final ApplicationContext context ) {
		final Timeout timeout = AnnotationUtils.findAnnotation(this.method, Timeout.class);
		if (timeout != null) {
			final String string = timeout.string();
			if (string != null && !string.isEmpty()) {
				try {
					final Object evaluated = this.evalutate( string, context );
					if (evaluated instanceof Number) {
						return ((Number)evaluated).longValue();
					} else if (evaluated instanceof String) {
						return Long.parseLong((String)evaluated);
					}
				} catch (Exception e) {
					throw new IllegalTimeoutStringException(string, method, e);
				}
				throw new IllegalTimeoutStringException(string, method);
			} else {
				return timeout.value();
			}
		}
		return 5000;
	}

	private Object evalutate( final String value, final ApplicationContext context ) {
		if (context instanceof ConfigurableApplicationContext) {
			final ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext)context;
			final ConfigurableBeanFactory configurableBeanFactory = configurableContext.getBeanFactory();
			final String placeholdersResolved = configurableBeanFactory.resolveEmbeddedValue(value);
			final BeanExpressionResolver exprResolver = configurableBeanFactory.getBeanExpressionResolver();
			final BeanExpressionContext expressionContext = new BeanExpressionContext(configurableBeanFactory, null);
			return exprResolver.evaluate(placeholdersResolved, expressionContext);
		}
		return null;
	}

	JavaType[] toTypes( final Method method, final ResolvableType implementationType ){
		final Class<?>[] types = method.getParameterTypes();
		final JavaType[] result = new JavaType[ types.length ];
		for( int i=0; i<types.length; ++i ) {
			final ResolvableType resolved
				= ResolvableType.forMethodParameter(MethodParameters.forMethod(method, i), implementationType);
			result[ i ] = Json.mapper.constructType(resolved.getType());
		}
		return result;
	}

	public Object[] cast( final Object[] parameters ){
		return cast( types, parameters );
	}

	public static Object[] cast( final JavaType[] types, final Object[] parameters ) {
		if( types.length != parameters.length ) return null;

		final Object[] result = new Object[ parameters.length ];
		for( int i=0; i<types.length; ++i ){
			final JavaType type = types[ i ];
			final Object parameter = parameters[ i ];

			// Jackson does not handle the primitive types correctly if the given value is null.
			// Thus, need to check if the parameter is null first.
			// See https://github.com/FasterXML/jackson-databind/issues/1433
			if( parameter == null ) {
				if( type.isPrimitive() ) {
					return null;
				} else {
					result[ i ] = parameter;
				}
			} else {
				try {
					result[ i ] = Json.mapper.convertValue( parameter, type );
				} catch( final Exception e ) {
					return null;
				}
			}
		}
		return result;
	}
}
