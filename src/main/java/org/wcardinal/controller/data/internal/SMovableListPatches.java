/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

public abstract class SMovableListPatches<V> extends SListPatches<V, SMovableListPatch<V>> {
	public void move( final long revision, final int oldIndex, final int newIndex ) {
		getOrCreate( revision ).move( oldIndex, newIndex );
	}
}
