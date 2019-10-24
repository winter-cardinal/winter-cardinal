/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import org.springframework.core.ResolvableType;

public class SMapDataImpl<V> extends SMapData<V, SMapPatch<V>, SMapPatches<V, SMapPatch<V>>> {
	public SMapDataImpl( final SMapContainer<V> scontainer, final Object origin ) {
		super(
			scontainer,
			origin,
			ResolvableType.forClassWithGenerics(SMapPatchesPackedImpl.class, scontainer.getType())
		);
	}

	@Override
	SMapPatches<V, SMapPatch<V>> newPatches() {
		return new SMapPatchesImpl<V>();
	}
}
