/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Map;
import java.util.NavigableMap;

import org.wcardinal.controller.data.SMap.Update;

public interface SMapPatch<V> extends SPatch {
	void put( String key, V value );
	void putAll( Map<? extends String, ? extends V> values );
	void remove( String key );
	void removeAll( Map<? extends String, ? extends V> values );
	void apply( NavigableMap<String, V> map, NavigableMap<String, V> padded, NavigableMap<String, V> premoved, NavigableMap<String, Update<V>> pupdated );
}
