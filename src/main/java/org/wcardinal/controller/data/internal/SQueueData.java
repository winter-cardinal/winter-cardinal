/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;

import org.springframework.core.ResolvableType;

public abstract class SQueueData<V, P extends SQueuePatch<V>, S extends SQueuePatches<V, P>> extends SContainerData<SQueueValues<V>, P, S> {
	public SQueueData( final SContainer<SQueueValues<V>, SPatchesPacked<SQueueValues<V>, P>> scontainer, final Object origin, final ResolvableType $bType ) {
		super( scontainer, origin, $bType );
	}

	boolean onAdd( final V value ) {
		patches.add( scontainer.getRevision(), value );
		$a.set( $a );
		return true;
	}

	boolean onAddAll( final Collection<? extends V> values ) {
		if( values.isEmpty() != true ) {
			patches.addAll( scontainer.getRevision(), values );
			$a.set( $a );
			return true;
		}
		return false;
	}

	void onRemove() {
		patches.remove( scontainer.getRevision() );
		$a.set( $a );
	}

	void onCapacity( final int capacity ) {
		patches.capacity( scontainer.getRevision(), capacity );
		$a.set( $a );
	}
}
