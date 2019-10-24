/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;

public interface SQueueContainer<V> extends SContainer<SQueueValues<V>, SPatchesPacked<SQueueValues<V>, SQueuePatch<V>>> {
	boolean onAdd( final V value );
	boolean onAddAll( final Collection<? extends V> values );
	void onRemove();
	void onClear();
	void onChange();
	void onInitialize( final Object except );
	void onCapacity( final int capacity );
}
