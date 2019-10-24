/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import org.springframework.core.ResolvableType;

public class SQueueDataImpl<V> extends SQueueData<V, SQueuePatch<V>, SQueuePatches<V, SQueuePatch<V>>> {
	public SQueueDataImpl( final SQueueContainer<V> scontainer, final Object origin ) {
		super(
			scontainer,
			origin,
			ResolvableType.forClassWithGenerics(SQueuePatchesPackedImpl.class, scontainer.getType())
		);
	}

	@Override
	SQueuePatches<V, SQueuePatch<V>> newPatches() {
		return new SQueuePatchesImpl<V>();
	}
}
