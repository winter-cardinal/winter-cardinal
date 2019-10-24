/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import org.wcardinal.controller.annotation.Debounced;
import org.wcardinal.controller.annotation.Decoratable;
import org.wcardinal.controller.annotation.Throttled;
import org.wcardinal.exception.IllegalDecorationException;

public abstract class MethodWrapperDecorator<T> {
	final static Logger logger = LoggerFactory.getLogger(MethodWrapperDecorator.class);

	final MethodWrapper<T> wrapper;

	public MethodWrapperDecorator( final MethodWrapper<T> wrapper ){
		this.wrapper = wrapper;
	}

	public static <T> MethodWrapperDecorator<T> create( final MethodWrapper<T> wrapper ){
		final Method method = wrapper.method;

		// Debounced
		final Debounced debounced = AnnotationUtils.findAnnotation(method, Debounced.class);
		if( debounced != null ){
			final Decoratable decoratable = AnnotationUtils.findAnnotation(method, Decoratable.class);
			if( decoratable != null ){
				return new MethodWrapperDecoratorDebounced<T>( wrapper, debounced );
			} else {
				throw new IllegalDecorationException( String.format( "@Debounced can not be applied to method '%s' having no annotations annotated with @Decoratable meta-annotation", method ) );
			}
		}

		// Throttled
		final Throttled throttled = AnnotationUtils.findAnnotation(method, Throttled.class);
		if( throttled != null ){
			final Decoratable decoratable = AnnotationUtils.findAnnotation(method, Decoratable.class);
			if( decoratable != null ){
				return new MethodWrapperDecoratorThrottled<T>( wrapper, throttled );
			} else {
				throw new IllegalDecorationException( String.format( "@Throttled can not be applied to method '%s' having no annotations annotated with @Decoratable meta-annotation", method ) );
			}
		}

		// Identity
		return new MethodWrapperDecoratorIdentity<T>( wrapper );
	}

	public abstract MethodResult<T> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Object instance, final Object[] parameters );

	public abstract void init( final MethodContainer container );
}
