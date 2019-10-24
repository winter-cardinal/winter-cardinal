/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ResolvableType;

import org.wcardinal.controller.internal.Properties;
import org.wcardinal.controller.internal.Property;
import org.wcardinal.controller.internal.info.StaticDataTask;

public class TaskMethods<T> extends AbstractCallableMethods<CallableMethod<T>, T> {
	public MethodResult<T> call( final MethodContainer container, final String name, final TrackingData trackingData, final MethodHook hook, final EnumSet<LockRequirement> lockRequirements, final T instance, final Object[] parameters ){
		if( instance != null ){
			final Methods<CallableMethod<T>> methods = typeToMethods.get(name);
			if( methods != null ) {
				for( final CallableMethod<T> method: methods.nonStatics ){
					if( lockRequirements.contains(method.lockRequirement) ) {
						final Object[] casted = method.cast(parameters);
						if( casted != null ){
							return method.call(container, trackingData, hook, instance, casted);
						}
					}
				}

				for( final CallableMethod<T> method: methods.statics ){
					if( lockRequirements.contains(method.lockRequirement) ) {
						final Object[] casted = method.cast(parameters);
						if( casted != null ){
							return method.call(container, trackingData, hook, null, casted);
						}
					}
				}
			}
		}

		return null;
	}

	public static <T> TaskMethods<T> create( final Collection<Method> methods, final ResolvableType implementationType ){
		final TaskMethods<T> result = new TaskMethods<>();

		for( final Method method: methods ){
			result.add(new CallableMethod<T>(method, implementationType));
		}

		return result;
	}

	public Map<String, StaticDataTask> toDataMap(){
		final Map<String, StaticDataTask> result = new HashMap<>();

		for( Map.Entry<String, Methods<CallableMethod<T>>> entry: typeToMethods.entrySet() ){
			long timeout = 0;
			final EnumSet<Property> properties = Properties.empty();
			final Methods<CallableMethod<T>> methods = entry.getValue();
			for( final CallableMethod<T> method: methods.nonStatics ){
				timeout = Math.max(timeout, method.timeout);
				if( method.ajax ) properties.add( Property.AJAX );
				if( method.readonly ) properties.add( Property.READ_ONLY );
				if( method.nonnull ) properties.add( Property.NON_NULL );
				if( method.historical ) properties.add( Property.HISTORICAL );
			}
			for( final CallableMethod<T> method: methods.statics ){
				timeout = Math.max(timeout, method.timeout);
				if( method.ajax ) properties.add( Property.AJAX );
				if( method.readonly ) properties.add( Property.READ_ONLY );
				if( method.nonnull ) properties.add( Property.NON_NULL );
				if( method.historical ) properties.add( Property.HISTORICAL );
			}
			result.put(entry.getKey(), new StaticDataTask( properties ));
		}

		return result;
	}
}
