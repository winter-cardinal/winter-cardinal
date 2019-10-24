/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

public class SQueuePatchesPacked<V, P extends SQueuePatch<V>> extends SPatchesPackedImpl<SQueueValues<V>, P> {
	public SQueuePatchesPacked( final long startRevision, final long endRevision, final P reset ) {
		super( startRevision, endRevision, reset );
	}

	public SQueuePatchesPacked( final long startRevision, final long endRevision, final NavigableMap<Long, P> revisionToMap ) {
		super( startRevision, endRevision, revisionToMap );
	}

	@Override
	public SChange apply( final long revision, final SQueueValues<V> queue ) {
		final List<V> added = new ArrayList<>();
		final List<V> removed = new ArrayList<>();
		if( reset != null ) {
			reset.apply( queue, added, removed );
		} else {
			for( final P patch: revisionToMap.tailMap( Math.max( revision, startRevision ) ).values() ) {
				patch.apply( queue, added, removed );
			}
		}
		if( added.isEmpty() && removed.isEmpty() ) {
			return null;
		} else {
			return new SChangeTwoStates( added, removed );
		}
	}
}
