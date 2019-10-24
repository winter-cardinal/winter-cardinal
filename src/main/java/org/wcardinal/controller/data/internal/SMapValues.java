/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

class SMapValues<V> implements Collection<V> {
	final SMapContainer<V> container;
	final Map<String, V> map;
	final Map<String, V> internalMap;
	final AutoCloseableReentrantLock lock;

	SMapValues( final SMapContainer<V> container, final Map<String, V> map, final Map<String, V> internalMap ){
		this.container = container;
		this.map = map;
		this.internalMap = internalMap;
		this.lock = container.getLock();
	}

	@Override
	public boolean add( final V value ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll( final Collection<? extends V> values ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean contains( final Object value ) {
		return map.containsValue(value);
	}

	@Override
	public boolean containsAll(final Collection<?> values) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			boolean result = true;
			for( final Object value: values ){
				if( internalMap.containsValue(value) != true ) {
					result = false;
					break;
				}
			}
			return result;
		}
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<V> iterator() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SMapValuesIterator<V>( container, map, internalMap );
		}
	}

	@Override
	public boolean remove( final Object o ) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			final Iterator<Map.Entry<String, V>> i = internalMap.entrySet().iterator();
			while( i.hasNext() ){
				final Map.Entry<String, V> entry = i.next();
				final String key = entry.getKey();
				final V value = entry.getValue();
				if( Objects.equals(value, o) ) {
					i.remove();
					container.onRemove( key );
					result = true;
					break;
				}
			}
			if( result ) container.onChange();
			return result;
		}
	}

	@Override
	public boolean removeAll(final Collection<?> values) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			final Iterator<Map.Entry<String, V>> i = internalMap.entrySet().iterator();
			while( i.hasNext() ){
				final Map.Entry<String, V> entry = i.next();
				final String key = entry.getKey();
				final V value = entry.getValue();
				if( values.contains(value) ) {
					i.remove();
					container.onRemove( key );
					result = true;
				}
			}
			if( result ) container.onChange();
			return result;
		}
	}

	@Override
	public boolean retainAll(final Collection<?> values) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			final Iterator<Map.Entry<String, V>> i = internalMap.entrySet().iterator();
			while( i.hasNext() ){
				final Map.Entry<String, V> entry = i.next();
				final String key = entry.getKey();
				final V value = entry.getValue();
				if( values.contains(value) != true ) {
					i.remove();
					container.onRemove( key );
					result = true;
				}
			}
			if( result ) container.onChange();
			return result;
		}
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Object[] toArray() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalMap.values().toArray();
		}
	}

	@Override
	public <T> T[] toArray(final T[] array) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalMap.values().toArray(array);
		}
	}

	@Override
	public String toString(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalMap.values().toString();
		}
	}

	@Override
	public boolean equals( final Object other ){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalMap.values().equals( other );
		}
	}
}
