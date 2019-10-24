/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Iterator;
import java.util.Map;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

class SMapKeySetIterator<V> implements Iterator<String> {
	final SMapContainer<V> container;
	final Map<String, V> map;
	final Map<String, V> internalMap;
	final AutoCloseableReentrantLock lock;
	final Iterator<Map.Entry<String, V>> iterator;
	Map.Entry<String, V> current = null;

	SMapKeySetIterator( final SMapContainer<V> container, final Map<String, V> map, final Map<String, V> internalMap ) {
		this.container = container;
		this.map = map;
		this.internalMap = internalMap;
		this.lock = container.getLock();
		iterator = internalMap.entrySet().iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public String next() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			current = iterator.next();
			return current.getKey();
		}
	}

	@Override
	public void remove() {
		try( final Unlocker unlocker = container.lock() ) {
			if( current != null ) {
				final String key = current.getKey();
				iterator.remove();
				container.onRemove( key );
				container.onChange();
				current = null;
			}
		}
	}
}
