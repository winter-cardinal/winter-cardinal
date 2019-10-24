/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.wcardinal.controller.data.SList.Update;

public class SListPatchesPacked<V, P extends SListPatch<V>> extends SPatchesPackedImpl<List<V>, P> {
	public SListPatchesPacked( final long startRevision, final long endRevision, final P reset ) {
		super( startRevision, endRevision, reset );
	}

	public SListPatchesPacked( final long startRevision, final long endRevision, final NavigableMap<Long, P> revisionToMap ) {
		super( startRevision, endRevision, revisionToMap );
	}

	@Override
	public SChange apply( final long revision, final List<V> list ) {
		final NavigableMap<Integer, V> added = new TreeMap<>();
		final NavigableMap<Integer, V> removed = new TreeMap<>();
		final NavigableMap<Integer, Update<V>> updated = new TreeMap<>();
		if( reset != null ) {
			reset.apply( list, added, removed, updated );
		} else {
			for( final P patch: revisionToMap.tailMap( Math.max( revision, startRevision ) ).values() ) {
				patch.apply( list, added, removed, updated );
			}
		}
		if( added.isEmpty() && removed.isEmpty() ) {
			return null;
		} else {
			return new SChangeThreeStates( added, removed, updated );
		}
	}
}
