/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Map;
import java.util.NavigableMap;

public abstract class SMapPatches<V, P extends SMapPatch<V>> extends SPatches<NavigableMap<String, V>, P> {
	public void put( final long revision, final String key, final V value ) {
		getOrCreate( revision ).put( key, value );
	}

	public void putAll( final long revision, final Map<? extends String, ? extends V> values ) {
		getOrCreate( revision ).putAll( values );
	}

	public void remove( final long revision, final String key ) {
		getOrCreate( revision ).remove( key );
	}

	public void removeAll( final long revision, final Map<? extends String, ? extends V> values ) {
		getOrCreate( revision ).removeAll( values );
	}

	@Override
	int size( final NavigableMap<String, V> value ) {
		return value.size();
	}
}
