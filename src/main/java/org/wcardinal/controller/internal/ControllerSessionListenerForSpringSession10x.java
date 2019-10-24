/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ClassUtils;

abstract class ControllerSessionListenerForSpringSession10x implements ApplicationListener<ApplicationEvent> {
	final Logger logger = LoggerFactory.getLogger(ControllerSessionListenerForSpringSession10x.class);

	final Class<?> klass;
	final Method method;

	ControllerSessionListenerForSpringSession10x( final String path ){
		klass = findClass( path );
		method = findMethod(klass, "getSessionId");
	}

	Method findMethod( final Class<?> klass, final String name ){
		if( klass == null ) return null;
		return ClassUtils.getMethod(klass, name);
	}

	Class<?> findClass( final String path ){
		try {
			return ClassUtils.forName(path, ControllerSessionListenerForSpringSession10x.class.getClassLoader());
		} catch (ClassNotFoundException | LinkageError e) {
			return null;
		}
	}

	String getSessionId( final ApplicationEvent event ){
		if( klass.isInstance(event) ){
			try {
				return (String) method.invoke(event);
			} catch ( final Exception e) {
				logger.error("Unexpected error", e);
			}
		}

		return null;
	}
}
