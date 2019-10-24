/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import org.springframework.core.ResolvableType;

public class SListDataImpl<V> extends SListData<V, SListPatch<V>, SListPatches<V, SListPatch<V>>> {
	public SListDataImpl( final SListContainer<V> scontainer, final Object origin ) {
		super(
			scontainer,
			origin,
			ResolvableType.forClassWithGenerics(SListPatchesPackedImpl.class, scontainer.getType())
		);
	}

	@Override
	SListPatches<V, SListPatch<V>> newPatches() {
		return new SListPatchesImpl<V>();
	}
}
