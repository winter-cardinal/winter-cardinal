/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class AbstractMethodAndOrders<T extends AbstractMethodAndOrder<U>, U> extends AbstractMethods<T> {
	final Set<T> methods = new HashSet<>();
	final Set<T> staticMethods = new HashSet<>();

	@Override
	public boolean add( final T methodAndOrder ){
		super.add( methodAndOrder );
		if( Modifier.isStatic(methodAndOrder.method.getModifiers()) ){
			return staticMethods.add( methodAndOrder );
		} else {
			return methods.add( methodAndOrder );
		}
	}

	public boolean containsNonStatic(){
		return methods.isEmpty() == false;
	}

	public TrackingData getTrackingData( final MethodContainer container, final Object instance ) {
		if( container == null || ! containsTrackable() ) return null;

		final TrackingData result = new TrackingData();

		if( instance != null ){
			for( final T wrapper: methods ){
				if( wrapper.isTrackable ) {
					result.put(wrapper, wrapper.getTrackingId(container, instance));
				}
			}
		}

		for( final T wrapper: staticMethods ){
			if( wrapper.isTrackable ) {
				result.put(wrapper, wrapper.getTrackingId(container, null));
			}
		}

		return result;
	}

	public void init( final MethodContainer container ){
		for( final T method: methods ){
			method.init( container );
		}

		for( final T method: staticMethods ){
			method.init( container );
		}
	}
}
