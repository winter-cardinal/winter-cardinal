/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import org.wcardinal.controller.annotation.Throttled;

public class MethodWrapperDecoratorThrottled<T> extends MethodWrapperDecoratorDebounced<T> {
	public MethodWrapperDecoratorThrottled( final MethodWrapper<T> wrapper, final Throttled throttled ){
		super( wrapper, throttled.interval(), throttled.leading(), throttled.trailing(), throttled.interval() );
	}
}
