/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.core.ResolvableType;

public class MethodAndOrders<T> extends AbstractMethodAndOrders<MethodAndOrder<T>, T>{
	public Collection<MethodResult<T>> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final EnumSet<LockRequirement> lockRequirements, final Object instance, final Object... parameters ){
		final Collection<MethodResult<T>> result = new ArrayList<>();

		if( instance != null ){
			for( final MethodAndOrder<T> order: methods ){
				if( lockRequirements.contains(order.lockRequirement) ) {
					result.add(order.call(container, trackingData, hook, instance, parameters));
				}
			}
		}

		for( final MethodAndOrder<T> order: staticMethods ){
			if( lockRequirements.contains(order.lockRequirement) ) {
				result.add(order.call(container, trackingData, hook, null, parameters));
			}
		}

		return result;
	}

	public static <T> MethodAndOrders<T> create( final Set<Method> methods, final ResolvableType implementationType, final ResolvableType... callerTypes ){
		final MethodAndOrders<T> result = new MethodAndOrders<T>();

		for( final Method method: methods ){
			final int[] order = getParameterOrder(method, implementationType, callerTypes);
			if( order != null ) {
				result.add(new MethodAndOrder<T>(method, order));
			}
		}

		return result;
	}
}
