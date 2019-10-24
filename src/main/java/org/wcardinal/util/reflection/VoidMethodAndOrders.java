/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.core.ResolvableType;

public class VoidMethodAndOrders extends AbstractMethodAndOrders<VoidMethodAndOrder, Void>{
	public void call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final EnumSet<LockRequirement> lockRequirements, final Object instance, final Object... parameters ){
		if( instance != null ){
			for( final VoidMethodAndOrder order: methods ){
				if( lockRequirements.contains(order.lockRequirement) ) {
					order.call(container, trackingData, hook, instance, parameters);
				}
			}
		}

		for( final VoidMethodAndOrder order: staticMethods ){
			if( lockRequirements.contains(order.lockRequirement) ) {
				order.call(container, trackingData, hook, null, parameters);
			}
		}
	}

	public static VoidMethodAndOrders create( final Set<Method> methods, final ResolvableType implementationType, final ResolvableType... callerTypes ){
		final VoidMethodAndOrders result = new VoidMethodAndOrders();

		for( final Method method: methods ){
			final int[] order = getParameterOrder( method, implementationType, callerTypes );
			if( order != null ) {
				result.add(new VoidMethodAndOrder( method, order ));
			}
		}

		return result;
	}
}
