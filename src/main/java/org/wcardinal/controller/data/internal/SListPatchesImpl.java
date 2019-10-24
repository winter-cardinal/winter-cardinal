/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

public class SListPatchesImpl<V> extends SListPatches<V, SListPatch<V>> {
	final SListPatchMap<V> PATCH_MAP_DUMMY = new SListPatchMap<>();

	@Override
	SListPatch<V> newPatchMap() {
		return new SListPatchMap<V>();
	}

	@Override
	SListPatch<V> getPatchMapDummy() {
		return PATCH_MAP_DUMMY;
	}

	@Override
	SListPatch<V> newPatchReset() {
		return new SListPatchReset<V>( null );
	}

	@Override
	SListPatch<V> newPatchReset( List<V> value ) {
		return new SListPatchReset<V>( new ArrayList<V>( value ) );
	}

	@Override
	SPatchesPacked<List<V>, SListPatch<V>> newPatchesPacked( final long authorizedRevision, final long revision, final SListPatch<V> patchReset ){
		return new SListPatchesPackedImpl<V>( authorizedRevision, revision, patchReset );
	}

	@Override
	SPatchesPacked<List<V>, SListPatch<V>> newPatchesPacked( final long authorizedRevision, final long revision, NavigableMap<Long, SListPatch<V>> revisionToPatchPacked ){
		return new SListPatchesPackedImpl<V>( authorizedRevision, revision, revisionToPatchPacked );
	}
}
