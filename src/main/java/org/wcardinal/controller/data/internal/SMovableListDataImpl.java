/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import org.springframework.core.ResolvableType;

public class SMovableListDataImpl<V> extends SMovableListData<V> {
	public SMovableListDataImpl( final SMovableListContainer<V> scontainer, final Object origin ) {
		super(
			scontainer,
			origin,
			ResolvableType.forClassWithGenerics(SMovableListPatchesPackedImpl.class, scontainer.getType())
		);
	}

	@Override
	SMovableListPatches<V> newPatches() {
		return new SMovableListPatchesImpl<V>();
	}
}
