/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

public class VoidParametrizedMethods extends AbstractMethodWrappers<VoidParametrizedMethod, Void>{
	public void call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final EnumSet<LockRequirement> lockRequirements, final Object instance, final Object... parameters ){
		if( instance != null ){
			for( final VoidParametrizedMethod method: methods ){
				if( lockRequirements.contains(method.lockRequirement) ) {
					container.handle( method.call(container, trackingData, hook, instance, parameters) );
				}
			}
		}

		for( final VoidParametrizedMethod method: staticMethods ){
			if( lockRequirements.contains(method.lockRequirement) ) {
				container.handle( method.call(container, trackingData, hook, null, parameters) );
			}
		}
	}

	public static VoidParametrizedMethods create( final Set<Method> methods ){
		final VoidParametrizedMethods result = new VoidParametrizedMethods();

		for( final Method method: methods ){
			result.add(new VoidParametrizedMethod( method ));
		}

		return result;
	}
}
