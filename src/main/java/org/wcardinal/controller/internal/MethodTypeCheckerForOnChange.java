/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

public abstract class MethodTypeCheckerForOnChange implements MethodTypeChecker {
	static final Logger logger = LoggerFactory.getLogger(MethodTypeCheckerForOnChange.class);

	final ResolvableType[] types;

	MethodTypeCheckerForOnChange( final ResolvableType... types ){
		this.types = types;
	}

	@Override
	public boolean check( final Method method, final ResolvableType implementationType ) {
		final int calleeSize = method.getParameterTypes().length;

		if( types.length < calleeSize ) {
			throwException( method );
			return false;
		}

		for( int i=0; i<calleeSize; ++i ) {
			final ResolvableType type = ResolvableType.forMethodParameter(new MethodParameter(method, i), implementationType);
			if( type.isAssignableFrom(types[i]) != true ) {
				throwException( method );
				return false;
			}
		}

		return true;
	}

	abstract void throwException( final Method method );
}
