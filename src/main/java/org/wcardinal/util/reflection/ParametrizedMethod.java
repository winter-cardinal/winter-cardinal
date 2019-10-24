/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

public class ParametrizedMethod<T> extends MethodWrapper<T> {
	private final int numberOfParameters;

	public ParametrizedMethod( final Method method ){
		super( method );
		this.numberOfParameters = method.getParameterTypes().length;
	}

	@Override
	public MethodResult<T> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Object instance, final Object[] parameters ){
		final Object[] _parameters = new Object[ numberOfParameters ];
		for( int i=0, imax=Math.min(numberOfParameters, parameters.length); i<imax; ++i ){
			_parameters[ i ] = parameters[ i ];
		}

		return super.call(container, trackingData, hook, instance, _parameters);
	}
}
