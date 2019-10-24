/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Map;
import java.util.Objects;

public class SCheckers {
	public static <V> void checkNonNull( final boolean isNonNull, final V value ){
		if( isNonNull ) {
			Objects.requireNonNull( value );
		}
	}

	public static <V> void checkNonNull( final boolean isNonNull, final Iterable<? extends V> values ){
		Objects.requireNonNull( values );
		if( isNonNull ) {
			for( final V value: values ){
				Objects.requireNonNull( value );
			}
		}
	}

	public static <V> boolean checkNonNullQuietly( final boolean isNonNull, final Iterable<? extends V> values ){
		if( isNonNull ) {
			for( final V value: values ){
				if( value == null ) return false;
			}
		}
		return true;
	}

	public static <V> void checkNonNull( final boolean isNonNull, final Map<? extends String, ? extends V> mappings ){
		Objects.requireNonNull( mappings );
		if( isNonNull ) {
			for( final Map.Entry<? extends String, ? extends V> entry: mappings.entrySet() ){
				Objects.requireNonNull( entry.getKey() );
				Objects.requireNonNull( entry.getValue() );
			}
		} else {
			for( final Map.Entry<? extends String, ? extends V> entry: mappings.entrySet() ){
				Objects.requireNonNull( entry.getKey() );
			}
		}
	}
}
