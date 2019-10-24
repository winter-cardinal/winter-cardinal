/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.annotation.AnnotationUtils;

public abstract class AbstractTypedMethods<T extends AbstractMethodWrappers<U, V>, U extends AbstractMethodWrapper<V>, V> extends AbstractMethods<U> {
	public final static String TYPE_ALL = "*";
	public final static String TYPE_SELF = ".";

	final T defaults = create();
	final Map<String, T> typeToMethods = new HashMap<>();

	public boolean add( final Class<? extends Annotation> annotationClass, final U wrapper, final boolean defaultsToMethodName ){
		final Object typesObject = AnnotationUtils.getValue(AnnotationUtils.findAnnotation(wrapper.method, annotationClass));
		if( (typesObject instanceof String[]) != true ) return false;
		final String[] types = (String[]) typesObject;

		if( types.length == 0 ){
			if( defaultsToMethodName ) {
				add( wrapper.method.getName(), wrapper );
			}
		} else {
			for( final String type: types ){
				if( Objects.equals( TYPE_ALL, type ) ) {
					add( wrapper );
					defaults.add( wrapper );
				} else {
					add( type, wrapper );
				}
			}
		}
		return true;
	}

	public boolean add( final String type, final U wrapper ){
		T wrappers = typeToMethods.get(type);
		if( wrappers == null ){
			wrappers = create();
			typeToMethods.put( type, wrappers );
		}
		return wrappers.add(wrapper);
	}

	public boolean containsNonStatic(){
		if( defaults.containsNonStatic() ) return true;
		for( final T wrappers: typeToMethods.values() ){
			if( wrappers.containsNonStatic() ) return true;
		}
		return false;
	}

	public boolean contains( final String type ){
		final T wrappers = typeToMethods.get( type );
		if( wrappers == null ) return false;
		return wrappers.contains();
	}

	public boolean containsLockUnspecified( final String type ){
		final T wrappers = typeToMethods.get( type );
		if( wrappers == null ) return false;
		return wrappers.hasLockUnspecified;
	}

	public boolean containsLockRequired( final String type ){
		final T wrappers = typeToMethods.get( type );
		if( wrappers == null ) return false;
		return wrappers.hasLockRequired;
	}

	public boolean containsLockNotRequired( final String type ){
		final T wrappers = typeToMethods.get( type );
		if( wrappers == null ) return false;
		return wrappers.hasLockNotRequired;
	}

	public boolean containsTrackable( final String type ){
		final T wrappers = typeToMethods.get( type );
		if( wrappers == null ) return false;
		return wrappers.hasTrackable;
	}

	public TrackingData getTrackingData( final String type, final MethodContainer container, final Object instance ) {
		if( container == null ) return null;

		final boolean containsTrackable = containsTrackable();
		final boolean containsTrackableOfType = containsTrackable( type );

		if( containsTrackable == false && containsTrackableOfType == false ) return null;

		final TrackingData result = new TrackingData();
		if( containsTrackable ) getTrackingDataDefaults( result, container, instance );
		if( containsTrackableOfType ) getTrackingDataNonDefaults( result, type, container, instance );
		return result;
	}

	public TrackingData getTrackingDataDefaults( final MethodContainer container, final Object instance ) {
		if( container == null || !containsTrackable() ) return null;

		final TrackingData result = new TrackingData();
		getTrackingDataDefaults( result, container, instance );
		return result;
	}

	public TrackingData getTrackingDataNonDefaults( final String type, final MethodContainer container, final Object instance ) {
		if( container == null || !containsTrackable( type ) ) return null;

		final TrackingData result = new TrackingData();
		getTrackingDataNonDefaults( result, type, container, instance );
		return result;
	}

	private TrackingData getTrackingDataDefaults( final TrackingData trackingData, final MethodContainer container, final Object instance ) {
		if( instance != null ){
			for( final U wrapper: defaults.methods ){
				if( wrapper.isTrackable ) {
					trackingData.put(wrapper, wrapper.getTrackingId(container, instance));
				}
			}
		}

		for( final U wrapper: defaults.staticMethods ){
			if( wrapper.isTrackable ) {
				trackingData.put(wrapper, wrapper.getTrackingId(container, null));
			}
		}

		return trackingData;
	}

	private TrackingData getTrackingDataNonDefaults( final TrackingData trackingData, final String type, final MethodContainer container, final Object instance ) {
		final T wrappers = typeToMethods.get(type);

		if( instance != null ){
			for( final U wrapper: wrappers.methods ){
				if( wrapper.isTrackable ) {
					trackingData.put(wrapper, wrapper.getTrackingId(container, instance));
				}
			}
		}

		for( final U wrapper: wrappers.staticMethods ){
			if( wrapper.isTrackable ) {
				trackingData.put(wrapper, wrapper.getTrackingId(container, null));
			}
		}

		return trackingData;
	}

	abstract T create();

	public void init( final MethodContainer container ){
		defaults.init( container );

		for( final T methods: typeToMethods.values() ){
			methods.init( container );
		}
	}
}
