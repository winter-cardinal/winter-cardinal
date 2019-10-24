/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

class SMapKeySet<V> implements Set<String> {
	final SMapContainer<V> container;
	final Map<String, V> map;
	final Map<String, V> internalMap;
	final AutoCloseableReentrantLock lock;

	SMapKeySet( final SMapContainer<V> container, final Map<String, V> map, final Map<String, V> internalMap ) {
		this.container = container;
		this.map = map;
		this.internalMap = internalMap;
		this.lock = container.getLock();
	}

	@Override
	public boolean add( final String key ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll( final Collection<? extends String> keys ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean contains( final Object key ) {
		return map.containsKey( key );
	}

	@Override
	public boolean containsAll( final Collection<?> keys ) {
		if( keys == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			boolean result = true;
			for( final Object key: keys ) {
				if( internalMap.containsKey( key ) != true ) {
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
	public Iterator<String> iterator() {
		return new SMapKeySetIterator<V>( container, map, internalMap );
	}

	@Override
	public boolean remove( final Object o ) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = internalMap.containsKey( o );
			if( result ) {
				map.remove( o );
			}
			return result;
		}
	}

	@Override
	public boolean removeAll( final Collection<?> c ) {
		if( c == null ) throw new NullPointerException();

		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			for( final Object element: c ){
				result |= remove( element );
			}
			return result;
		}
	}

	@Override
	public boolean retainAll( final Collection<?> c ) {
		if( c == null ) throw new NullPointerException();

		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			final Iterator<Map.Entry<String, V>> itr = internalMap.entrySet().iterator();
			while( itr.hasNext() ){
				final String key = itr.next().getKey();
				if( c.contains(key) != true ){
					itr.remove();
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
			return internalMap.keySet().toArray();
		}
	}

	@Override
	public <T> T[] toArray(final T[] array) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalMap.keySet().toArray(array);
		}
	}

	@Override
	public String toString(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalMap.keySet().toString();
		}
	}

	@Override
	public boolean equals( final Object other ){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalMap.keySet().equals( other );
		}
	}
}
