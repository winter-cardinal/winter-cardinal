/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.List;
import java.util.NavigableMap;

import org.wcardinal.controller.data.SMovableList.Move;
import org.wcardinal.controller.data.SList.Update;

public interface SMovableListPatch<V> extends SListPatch<V> {
	void move( int oldIndex, int newIndex );
	void apply( final List<V> list, final NavigableMap<Integer, V> padded, final NavigableMap<Integer, V> premoved,
		final NavigableMap<Integer, Update<V>> pupdated, final List<Move<V>> pnewMoved );
}
