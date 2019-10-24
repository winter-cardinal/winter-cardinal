/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.EnumSet;

import org.wcardinal.controller.annotation.ReadOnly;
import org.wcardinal.controller.data.annotation.Historical;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.controller.data.annotation.Soft;
import org.wcardinal.controller.data.annotation.Uninitialized;

public class Properties {
	static boolean is( final Class<?> type, final Class<? extends Annotation> annotationClass ) {
		return type.isAnnotationPresent(annotationClass);
	}

	static boolean is( final AccessibleObject object, final Class<? extends Annotation> annotationClass ) {
		return object.isAnnotationPresent(annotationClass);
	}

	static void addIf( final EnumSet<Property> properties, final boolean check, final Property property ) {
		if( check ) properties.add( property );
	}

	static public EnumSet<Property> create(
			final boolean isShared,
			final boolean isFactory,
			final boolean isReadOnly,
			final boolean isHistorical,
			final boolean isNonNull,
			final boolean isUninitialized,
			final boolean isSoft
	) {
		final EnumSet<Property> result = empty();
		addIf( result, isShared, Property.SHARED );
		addIf( result, isFactory, Property.FACTORY );
		addIf( result, isReadOnly, Property.READ_ONLY );
		addIf( result, isHistorical, Property.HISTORICAL );
		addIf( result, isNonNull, Property.NON_NULL );
		addIf( result, isUninitialized, Property.UNINITIALIZED );
		addIf( result, isSoft, Property.SOFT );
		return result;
	}

	static public EnumSet<Property> create( final AccessibleObject object ) {
		final EnumSet<Property> result = empty();
		addIf( result, is( object, ReadOnly.class ), Property.READ_ONLY );
		addIf( result, is( object, Historical.class ), Property.HISTORICAL );
		addIf( result, is( object, NonNull.class ), Property.NON_NULL );
		addIf( result, is( object, Uninitialized.class ), Property.UNINITIALIZED );
		addIf( result, is( object, Soft.class ), Property.SOFT );
		return result;
	}

	static public EnumSet<Property> create( final Class<?> type ) {
		final EnumSet<Property> result = empty();
		addIf( result, is( type, ReadOnly.class ), Property.READ_ONLY );
		addIf( result, is( type, Historical.class ), Property.HISTORICAL );
		addIf( result, is( type, NonNull.class ), Property.NON_NULL );
		addIf( result, is( type, Uninitialized.class ), Property.UNINITIALIZED );
		addIf( result, is( type, Soft.class ), Property.SOFT );
		return result;
	}

	static public EnumSet<Property> create( final EnumSet<Property> a, final EnumSet<Property> b ) {
		final EnumSet<Property> result = EnumSet.copyOf( a );
		result.addAll( b );
		return result;
	}

	static public EnumSet<Property> create( final EnumSet<Property> a, final Property b ) {
		final EnumSet<Property> result = EnumSet.copyOf( a );
		result.add( b );
		return result;
	}

	static public int toInt(EnumSet<Property> properties) {
		int result = 0;
		for( final Property property: properties ) {
			result |= (1 << property.ordinal());
		}
		return result;
	}

	static public EnumSet<Property> empty() {
		return EnumSet.noneOf(Property.class);
	}
}
