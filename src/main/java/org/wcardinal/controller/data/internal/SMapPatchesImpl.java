/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.NavigableMap;
import java.util.TreeMap;

public class SMapPatchesImpl<V> extends SMapPatches<V, SMapPatch<V>> {
	final SMapPatchMap<V> PATCH_MAP_DUMMY = new SMapPatchMap<>();

	@Override
	SMapPatch<V> newPatchMap() {
		return new SMapPatchMap<V>();
	}

	@Override
	SMapPatch<V> getPatchMapDummy() {
		return PATCH_MAP_DUMMY;
	}

	@Override
	SMapPatch<V> newPatchReset() {
		return new SMapPatchReset<V>( null );
	}

	@Override
	SMapPatch<V> newPatchReset( NavigableMap<String, V> value ) {
		return new SMapPatchReset<V>( new TreeMap<String, V>( value ) );
	}

	@Override
	SPatchesPacked<NavigableMap<String, V>, SMapPatch<V>> newPatchesPacked( final long authorizedRevision, final long revision, final SMapPatch<V> patchReset ){
		return new SMapPatchesPackedImpl<V>( authorizedRevision, revision, patchReset );
	}

	@Override
	SPatchesPacked<NavigableMap<String, V>, SMapPatch<V>> newPatchesPacked( final long authorizedRevision, final long revision, NavigableMap<Long, SMapPatch<V>> revisionToPatchPacked ){
		return new SMapPatchesPackedImpl<V>( authorizedRevision, revision, revisionToPatchPacked );
	}
}
