/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

public class VoidTypedParametrizedMethods extends AbstractTypedMethods<VoidParametrizedMethods, VoidParametrizedMethod, Void> {
	public void call( final MethodContainer container, final String type, final TrackingData trackingData, final MethodHook hook, final EnumSet<LockRequirement> lockRequirements, final Object instance, final Object... parameters ){
		defaults.call(container, trackingData, hook, lockRequirements, instance, parameters);

		final VoidParametrizedMethods wrappers = typeToMethods.get(type);
		if( wrappers != null ){
			wrappers.call(container, trackingData, hook, lockRequirements, instance, parameters);
		}
	}

	public void callDefaults( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final EnumSet<LockRequirement> lockRequirements, final Object instance, final Object... parameters ){
		defaults.call(container, trackingData, hook, lockRequirements, instance, parameters);
	}

	public void callNonDefaults( final MethodContainer container, final String type, final TrackingData trackingData, final MethodHook hook, final EnumSet<LockRequirement> lockRequirements, final Object instance, final Object... parameters ){
		final VoidParametrizedMethods wrappers = typeToMethods.get(type);
		if( wrappers != null ){
			wrappers.call(container, trackingData, hook, lockRequirements, instance, parameters);
		}
	}

	@Override
	VoidParametrizedMethods create() {
		return new VoidParametrizedMethods();
	}

	public static VoidTypedParametrizedMethods create( final Set<Method> methods, final Class<? extends Annotation> annotationClass ){
		return create( methods, annotationClass, false );
	}

	public static VoidTypedParametrizedMethods create( final Set<Method> methods, final Class<? extends Annotation> annotationClass, boolean defaultsToMethodName ){
		final VoidTypedParametrizedMethods result = new VoidTypedParametrizedMethods();

		for( final Method method: methods ){
			result.add(annotationClass, new VoidParametrizedMethod( method ), defaultsToMethodName);
		}

		return result;
	}
}
