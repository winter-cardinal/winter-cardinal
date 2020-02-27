/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.MethodCallback;

import org.wcardinal.controller.data.SKeyOf;
import org.wcardinal.util.Reference;

public class SKeyOfImpl<V> implements SKeyOf<V> {
	static final Logger logger = LoggerFactory.getLogger(SKeyOfImpl.class);

	private final Field idField;
	private final Method idMethod;

	public SKeyOfImpl( final Class<V> type ) {
		idField = getIdField( type );
		if( idField != null ) {
			ReflectionUtils.makeAccessible( idField );
			idMethod = null;
		} else {
			idMethod = getIdMethod( type );
			if( idMethod != null ) {
				ReflectionUtils.makeAccessible( idMethod );
			}
		}
	}

	Field getIdField( final Class<V> type ) {
		final Reference<Field> result = new Reference<>();
		ReflectionUtils.doWithFields(type, new FieldCallback(){
			@Override
			public void doWith( final Field field ) throws IllegalArgumentException, IllegalAccessException {
				final Annotation[] annotations = field.getDeclaredAnnotations();
				for( final Annotation annotation: annotations ) {
					if( "id".equalsIgnoreCase( annotation.annotationType().getSimpleName() ) ) {
						result.set( field );
					}
				}
			}
		});
		return result.get();
	}

	Method getIdMethod( final Class<V> type ) {
		final Reference<Method> result = new Reference<>();
		ReflectionUtils.doWithMethods(type, new MethodCallback(){
			@Override
			public void doWith( final Method method ) throws IllegalArgumentException, IllegalAccessException {
				final MergedAnnotations mergedAnnotations = MergedAnnotations.from( method );
				for( final MergedAnnotation<?> mergedAnnotation: mergedAnnotations ) {
					if( "id".equalsIgnoreCase( mergedAnnotation.getType().getSimpleName() ) ) {
						result.set( method );
					}
				}
			}
		});
		return result.get();
	}

	@Override
	public String keyOf( final V value ) {
		try {
			if( idField != null ) {
				final Object idValue = idField.get( value );
				if( idValue != null ) {
					return String.valueOf( idValue );
				} else {
					return null;
				}
			} else if( idMethod != null ){
				final Object idValue = idMethod.invoke( value );
				if( idValue != null ) {
					return String.valueOf( idValue );
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch ( final Exception e ) {
			return null;
		}
	}
}
