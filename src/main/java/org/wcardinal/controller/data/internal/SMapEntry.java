/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Map;
import java.util.Objects;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

class SMapEntry<V> implements Map.Entry<String, V> {
	final SMapContainer<V> container;
	final Map<String, V> map;
	final AutoCloseableReentrantLock lock;
	final String key;
	V value;

	SMapEntry( final Map.Entry<String, V> entry, final SMapContainer<V> container, final Map<String, V> map ){
		this.container = container;
		this.map = map;
		this.lock = container.getLock();
		key = entry.getKey();
		value = entry.getValue();
	}

	@Override
	public String getKey() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return key;
		}
	}

	@Override
	public V getValue() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return value;
		}
	}

	@Override
	public V setValue( final V value ) {
		try( final Unlocker unlocker = container.lock() ) {
			final V result = map.put(key, value);
			this.value = value;
			return result;
		}
	}

	@Override
	public String toString(){
		return "{\""+Objects.toString(key)+"\"=\"" + Objects.toString(value)+"\"}";
	}

	@Override
	public boolean equals( final Object other ) {
		if( other == null ) return false;
		if( other instanceof Map.Entry<?, ?> ){
			final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) other;
			final Object otherKey = entry.getKey();
			final Object otherValue = entry.getValue();
			return ( Objects.equals(key, otherKey) && Objects.equals(value, otherValue) );
		}
		return false;
	}

	static <V> SMapEntry<V> create( final Map.Entry<String, V> entry, final SMapContainer<V> container, final Map<String, V> map ) {
		if( entry != null ) {
			return new SMapEntry<V>( entry, container, map );
		} else {
			return null;
		}
	}
}
