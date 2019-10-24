/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

public class MethodWrapper<T> extends AbstractMethodWrapper<T> {
	final MethodWrapperDecorator<T> decorator;

	public MethodWrapper(final Method method) {
		super(method);
		decorator = MethodWrapperDecorator.create( this );
	}

	@Override
	public MethodResult<T> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Object instance, final Object[] parameters ) {
		return decorator.call( container, trackingData, hook, instance, parameters );
	}

	public MethodResult<T> callUndecorated( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Object instance, final Object[] parameters ) {
		return super.call(container, trackingData, hook, instance, parameters);
	}

	@Override
	public void init( final MethodContainer container ){
		super.init( container );
		decorator.init( container );
	}
}
