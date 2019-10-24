/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Map;
import java.util.NavigableMap;

import org.springframework.core.ResolvableType;

public abstract class SMapData<V, P extends SMapPatch<V>, S extends SMapPatches<V, P>> extends SContainerData<NavigableMap<String, V>, P, S> {
	public SMapData( final SContainer<NavigableMap<String, V>, SPatchesPacked<NavigableMap<String, V>, P>> scontainer, final Object origin, final ResolvableType $bType ) {
		super( scontainer, origin, $bType );
	}

	boolean onPut( final String key, final V value ) {
		patches.put( scontainer.getRevision(), key, value );
		$a.set( $a );
		return true;
	}

	boolean onPutAll( final Map<? extends String, ? extends V> values ) {
		if( values.isEmpty() != true ) {
			patches.putAll( scontainer.getRevision(), values );
			$a.set( $a );
			return true;
		}
		return false;
	}

	void onRemove( final String key ) {
		patches.remove( scontainer.getRevision(), key );
		$a.set( $a );
	}

	boolean onRemoveAll( final Map<? extends String, ? extends V> values ) {
		if( values.isEmpty() != true ) {
			patches.removeAll( scontainer.getRevision(), values );
			$a.set( $a );
			return true;
		}
		return false;
	}
}
