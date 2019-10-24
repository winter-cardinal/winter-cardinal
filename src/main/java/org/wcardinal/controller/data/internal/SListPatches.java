/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.List;

public abstract class SListPatches<V, P extends SListPatch<V>> extends SPatches<List<V>, P> {
	public void add( final long revision, final int index, final V value ) {
		getOrCreate( revision ).add( index, value );
	}

	public void addAll( final long revision, final int index, final Collection<? extends V> values ) {
		getOrCreate( revision ).addAll( index, values );
	}

	public void remove( final long revision, final int index ) {
		getOrCreate( revision ).remove( index );
	}

	public void set( final long revision, final int index, final V value ) {
		getOrCreate( revision ).set( index, value );
	}

	@Override
	int size( final List<V> value ) {
		return value.size();
	}
}
