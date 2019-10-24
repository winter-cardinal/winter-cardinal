/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Methods<T extends AbstractCallableMethod<?>> extends AbstractMethods<T> {
	final Set<T> nonStatics = new HashSet<>();
	final Set<T> statics = new HashSet<>();
}

public class AbstractCallableMethods<T extends AbstractCallableMethod<U>, U> extends AbstractMethods<T> {
	static final Logger logger = LoggerFactory.getLogger(AbstractCallableMethods.class);

	final Map<String, Methods<T>> typeToMethods = new HashMap<>();

	@Override
	public boolean add( final T callableMethod ){
		final Method method = callableMethod.method;
		final String name = method.getName();
		final boolean isStatic = Modifier.isStatic(method.getModifiers());
		final Map<String, Methods<T>> collection = typeToMethods;
		Methods<T> methods = collection.get(name);
		if( methods == null ){
			methods = new Methods<>();
			collection.put(name, methods);
		}
		if( isStatic ) {
			methods.statics.add( callableMethod );
			methods.add( callableMethod );
		} else {
			methods.nonStatics.add( callableMethod );
			methods.add( callableMethod );
		}
		super.add( callableMethod );
		return true;
	}

	public boolean containsLockUnspecified( final String name ){
		final Methods<T> methods = typeToMethods.get( name );
		if( methods == null ) return false;
		return methods.hasLockUnspecified;
	}

	public boolean containsLockRequired( final String name ){
		final Methods<T> methods = typeToMethods.get( name );
		if( methods == null ) return false;
		return methods.hasLockRequired;
	}

	public boolean containsLockNotRequired( final String name ){
		final Methods<T> methods = typeToMethods.get( name );
		if( methods == null ) return false;
		return methods.hasLockNotRequired;
	}

	public boolean containsTrackable( final String name ){
		final Methods<T> methods = typeToMethods.get( name );
		if( methods == null ) return false;
		return methods.hasTrackable;
	}

	public TrackingData getTrackingData( final String type, final MethodContainer container, final Object instance ) {
		if( container == null || !containsTrackable( type ) ) return null;

		final TrackingData result = new TrackingData();
		getTrackingData( result, type, container, instance );
		return result;
	}

	private TrackingData getTrackingData( final TrackingData trackingData, final String type, final MethodContainer container, final Object instance ) {
		final Methods<T> methods = typeToMethods.get(type);

		if( instance != null ){
			for( final T method: methods.nonStatics ){
				if( method.isTrackable ) {
					trackingData.put(method, method.getTrackingId(container, instance));
				}
			}
		}

		for( final T method: methods.statics ){
			if( method.isTrackable ) {
				trackingData.put(method, method.getTrackingId(container, null));
			}
		}

		return trackingData;
	}

	public Set<String> getTypes(){
		return typeToMethods.keySet();
	}

	public void init( final MethodContainer container ){
		for( final Methods<T> methods: typeToMethods.values() ){
			for( final T method: methods.nonStatics ){
				method.init( container );
			}

			for( final T method: methods.statics ){
				method.init( container );
			}
		}
	}
}
