/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

public class SMovableListPatchesImpl<V> extends SMovableListPatches<V> {
	final SMovableListPatchMap<V> PATCH_MAP_DUMMY = new SMovableListPatchMap<>();

	@Override
	SMovableListPatch<V> newPatchMap() {
		return new SMovableListPatchMap<V>();
	}

	@Override
	SMovableListPatch<V> getPatchMapDummy() {
		return PATCH_MAP_DUMMY;
	}

	@Override
	SMovableListPatch<V> newPatchReset() {
		return new SMovableListPatchReset<V>( null );
	}

	@Override
	SMovableListPatch<V> newPatchReset( List<V> value ) {
		return new SMovableListPatchReset<V>( new ArrayList<V>( value ) );
	}

	@Override
	SPatchesPacked<List<V>, SMovableListPatch<V>> newPatchesPacked( final long authorizedRevision, final long revision, final SMovableListPatch<V> patchReset ){
		return new SMovableListPatchesPackedImpl<V>( authorizedRevision, revision, patchReset );
	}

	@Override
	SPatchesPacked<List<V>, SMovableListPatch<V>> newPatchesPacked( final long authorizedRevision, final long revision, NavigableMap<Long, SMovableListPatch<V>> revisionToPatchPacked ){
		return new SMovableListPatchesPackedImpl<V>( authorizedRevision, revision, revisionToPatchPacked );
	}
}
