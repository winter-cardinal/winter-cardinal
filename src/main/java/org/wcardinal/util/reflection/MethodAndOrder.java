/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

public class MethodAndOrder<T> extends AbstractMethodAndOrder<T> {
	public MethodAndOrder( final Method method, final int[] order ){
		super( method, order );
	}

	@Override
	public MethodResult<T> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Object instance, final Object[] parameters ) {
		final Object[] _parameters = new Object[ order.length ];
		for( int i=0; i<order.length; ++i ){
			_parameters[i] = parameters[order[i]];
		}

		return super.call(container, trackingData, hook, instance, _parameters);
	}
}
