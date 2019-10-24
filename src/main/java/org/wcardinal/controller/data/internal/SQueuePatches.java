/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;

public abstract class SQueuePatches<V, P extends SQueuePatch<V>> extends SPatches<SQueueValues<V>, P> {
	public void add( final long revision, final V value ) {
		getOrCreate( revision ).add( value );
	}

	public void addAll( final long revision, final Collection<? extends V> values ) {
		getOrCreate( revision ).addAll( values );
	}

	public void remove( final long revision ) {
		getOrCreate( revision ).remove();
	}

	public void capacity( final long revision, final int capacity ) {
		getOrCreate( revision ).capacity( capacity );
	}

	@Override
	int size( final SQueueValues<V> value ) {
		return value.size();
	}
}
