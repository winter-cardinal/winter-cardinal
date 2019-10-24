/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.List;

import org.springframework.core.ResolvableType;

public abstract class SListData<V, P extends SListPatch<V>, S extends SListPatches<V, P>> extends SContainerData<List<V>, P, S> {
	public SListData( final SContainer<List<V>, SPatchesPacked<List<V>, P>> scontainer, final Object origin, final ResolvableType $bType ) {
		super( scontainer, origin, $bType );
	}

	boolean onAdd( final int index, final V value ) {
		patches.add( scontainer.getRevision(), index, value );
		$a.set( $a );
		return true;
	}

	boolean onAddAll( final int index, final Collection<? extends V> values ) {
		if( values.isEmpty() != true ) {
			patches.addAll( scontainer.getRevision(), index, values );
			$a.set( $a );
			return true;
		}
		return false;
	}

	V onRemove( final int index, final V value ) {
		patches.remove( scontainer.getRevision(), index );
		$a.set( $a );
		return value;
	}

	void onSet( final int index, final V newValue ) {
		patches.set( scontainer.getRevision(), index, newValue );
		$a.set( $a );
	}
}
