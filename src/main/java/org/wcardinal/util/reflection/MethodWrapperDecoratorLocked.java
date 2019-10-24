/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import org.wcardinal.util.thread.Unlocker;

public abstract class MethodWrapperDecoratorLocked<T> extends MethodWrapperDecorator<T> {
	static class LockableData {

	}

	public MethodWrapperDecoratorLocked(final MethodWrapper<T> wrapper) {
		super(wrapper);
	}

	public MethodResult<T> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final boolean isLocked, final Object instance, final Object[] parameters ) {
		if( isLocked ) {
			try( final Unlocker unlocker = container.lock() ) {
				return wrapper.callUndecorated(container, trackingData, hook, instance, parameters);
			}
		} else {
			return wrapper.callUndecorated(container, trackingData, hook, instance, parameters);
		}
	}
}
