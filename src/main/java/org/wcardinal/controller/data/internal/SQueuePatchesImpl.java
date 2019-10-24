/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.ArrayList;
import java.util.NavigableMap;

public class SQueuePatchesImpl<V> extends SQueuePatches<V, SQueuePatch<V>> {
	final SQueuePatchMap<V> PATCH_MAP_DUMMY = new SQueuePatchMap<>();

	@Override
	SQueuePatch<V> newPatchMap() {
		return new SQueuePatchMap<V>();
	}

	@Override
	SQueuePatch<V> getPatchMapDummy() {
		return PATCH_MAP_DUMMY;
	}

	@Override
	SQueuePatch<V> newPatchReset() {
		return new SQueuePatchReset<V>( null, -1 );
	}

	@Override
	SQueuePatch<V> newPatchReset( final SQueueValues<V> value ) {
		return new SQueuePatchReset<V>( new ArrayList<V>( value ), value.getCapacity() );
	}

	@Override
	SPatchesPacked<SQueueValues<V>, SQueuePatch<V>> newPatchesPacked( final long authorizedRevision, final long revision, final SQueuePatch<V> patchReset ){
		return new SQueuePatchesPackedImpl<V>( authorizedRevision, revision, patchReset );
	}

	@Override
	SPatchesPacked<SQueueValues<V>, SQueuePatch<V>> newPatchesPacked( final long authorizedRevision, final long revision, NavigableMap<Long, SQueuePatch<V>> revisionToPatchPacked ){
		return new SQueuePatchesPackedImpl<V>( authorizedRevision, revision, revisionToPatchPacked );
	}
}
