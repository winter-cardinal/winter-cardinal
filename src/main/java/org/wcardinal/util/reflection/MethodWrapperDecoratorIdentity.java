/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

public class MethodWrapperDecoratorIdentity<T> extends MethodWrapperDecorator<T> {
	public MethodWrapperDecoratorIdentity(final MethodWrapper<T> wrapper) {
		super(wrapper);
	}

	@Override
	public MethodResult<T> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Object instance, final Object[] parameters ) {
		return wrapper.callUndecorated(container, trackingData, hook, instance, parameters);
	}

	@Override
	public void init( final MethodContainer container ){
		// Do nothing
	}
}
