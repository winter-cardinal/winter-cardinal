/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;

import org.wcardinal.controller.data.SList.Update;

public interface SListPatch<V> extends SPatch {
	void add( int index, V value );
	void addAll( int index, Collection<? extends V> values );
	void remove( int index );
	void set( int index, V value );
	void apply( List<V> list, NavigableMap<Integer, V> padded, NavigableMap<Integer, V> premoved, NavigableMap<Integer, Update<V>> pupdated );
}
