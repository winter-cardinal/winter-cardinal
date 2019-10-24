/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.List;

public interface SListContainer<V> extends SContainer<List<V>, SPatchesPacked<List<V>, SListPatch<V>>> {
	boolean onAdd( final int index, final V value );
	boolean onAddAll( final int index, final Collection<? extends V> values );
	V onRemove( final int index, final V value );
	void onClear();
	void onSet( final int index, final V newValue );
	void onChange();
	void onInitialize( final Object except );
}
