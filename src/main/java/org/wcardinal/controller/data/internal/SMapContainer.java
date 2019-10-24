/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;

import org.wcardinal.controller.data.SLockable;

public interface SMapContainer<V> extends SContainer<NavigableMap<String, V>, SPatchesPacked<NavigableMap<String, V>, SMapPatch<V>>>, SLockable {
	boolean onPut( String key, V value );
	boolean onPutAll( Map<? extends String, ? extends V> values );
	void onRemove( String key );
	void onClear();
	void onInitialize( Object except );
	void onChange();
	Comparator<? super String> comparator();
}
