/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import org.springframework.core.ResolvableType;

public abstract class SMovableListData<V> extends SListData<V, SMovableListPatch<V>, SMovableListPatches<V>> {
	public SMovableListData( final SMovableListContainer<V> scontainer, final Object origin, final ResolvableType $bType ) {
		super(scontainer, origin, $bType);
	}

	void onMove( final int oldIndex, final int newIndex ) {
		patches.move( scontainer.getRevision(), oldIndex, newIndex );
		$a.set( $a );
	}
}
