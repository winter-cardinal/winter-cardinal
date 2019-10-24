/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.wcardinal.controller.data.SMovableList.Move;
import org.wcardinal.controller.data.SList.Update;

public class SMovableListPatchesPacked<V, P extends SMovableListPatch<V>> extends SListPatchesPacked<V, P> implements Comparator<Move<V>> {
	public SMovableListPatchesPacked( final long startRevision, final long endRevision, final P reset ) {
		super( startRevision, endRevision, reset );
	}

	public SMovableListPatchesPacked( final long startRevision, final long endRevision, final NavigableMap<Long, P> revisionToPatch ) {
		super( startRevision, endRevision, revisionToPatch );
	}

	@Override
	public SChange apply( final long revision, final List<V> list ) {
		final NavigableMap<Integer, V> added = new TreeMap<>();
		final NavigableMap<Integer, V> removed = new TreeMap<>();
		final NavigableMap<Integer, Update<V>> updated = new TreeMap<>();
		final List<Move<V>> newMoved = new ArrayList<>();
		if( reset != null ) {
			reset.apply( list, added, removed, updated, newMoved );
		} else {
			for( final P patch: revisionToMap.tailMap( Math.max( revision, startRevision ) ).values() ) {
				patch.apply( list, added, removed, updated, newMoved );
			}
		}
		if( added.isEmpty() && removed.isEmpty() && updated.isEmpty() && newMoved.isEmpty() ) {
			return null;
		} else {
			final List<Move<V>> oldMoved = new ArrayList<>( newMoved );
			Collections.sort( oldMoved, this );
			return new SChangeFourStates( added, removed, updated, newMoved, oldMoved );
		}
	}

	@Override
	public int compare( final Move<V> a, final Move<V> b ) {
		if( a != null ) {
			return ( b != null ? Integer.compare( a.getOldIndex(), b.getOldIndex() ) : +1 );
		} else {
			return ( b != null ? -1 : 0 );
		}
	}
}
