/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.wcardinal.controller.data.SMap.Update;

public class SMapPatchesPacked<V, P extends SMapPatch<V>> extends SPatchesPackedImpl<NavigableMap<String, V>, P> {
	public SMapPatchesPacked( final long startRevision, final long endRevision, final P reset ) {
		super( startRevision, endRevision, reset );
	}

	public SMapPatchesPacked( final long startRevision, final long endRevision, final NavigableMap<Long, P> revisionToMap ) {
		super( startRevision, endRevision, revisionToMap );
	}

	@Override
	public SChange apply( final long revision, final NavigableMap<String, V> map ) {
		final NavigableMap<String, V> added = new TreeMap<>( map.comparator() );
		final NavigableMap<String, V> removed = new TreeMap<>( map.comparator() );
		final NavigableMap<String, Update<V>> updated = new TreeMap<>( map.comparator() );
		if( reset != null ) {
			reset.apply( map, added, removed, updated );
		} else {
			for( final P patch: revisionToMap.tailMap( Math.max( revision, startRevision ) ).values() ) {
				patch.apply( map, added, removed, updated );
			}
		}
		if( added.isEmpty() && removed.isEmpty() ) {
			return null;
		} else {
			return new SChangeThreeStates( added, removed, updated );
		}
	}
}
