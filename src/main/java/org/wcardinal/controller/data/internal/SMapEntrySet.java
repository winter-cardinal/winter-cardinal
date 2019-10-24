/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

class SMapEntrySet<V> implements Set<Map.Entry<String, V>> {
	final SMapContainer<V> container;
	final Map<String, V> map;
	final Map<String, V> internalMap;
	final AutoCloseableReentrantLock lock;

	SMapEntrySet( final SMapContainer<V> container, final Map<String, V> map, final Map<String, V> internalMap ){
		this.container = container;
		this.map = map;
		this.internalMap = internalMap;
		this.lock = container.getLock();
	}

	boolean add( final String key, final V value ) {
		if( internalMap.containsKey( key ) ) {
			if( Objects.equals(internalMap.get( key ), value) != true ) {
				map.put( key, value );
				return true;
			}
		} else {
			map.put( key, value );
			return true;
		}
		return false;
	}

	@Override
	public boolean add(final Map.Entry<String, V> entry) {
		try( final Unlocker unlocker = container.lock() ) {
			return add( entry.getKey(), entry.getValue() );
		}
	}

	@Override
	public boolean addAll(final Collection<? extends Map.Entry<String, V>> map) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			for( final Map.Entry<String, V> entry: map ){
				result |= add( entry );
			}
			return result;
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	boolean contains( final Map.Entry<String, ? extends V> target ) {
		if( target != null ) {
			return Objects.equals( internalMap.get( target.getKey() ), target.getValue() );
		}
		return false;
	}

	@Override
	public boolean contains( final Object object ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return contains( toEntry( object ) );
		}
	}

	@Override
	public boolean containsAll( final Collection<?> objects ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			boolean result = true;
			for( final Object object: objects ){
				if( contains( object) != true ) {
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
	public Iterator<Map.Entry<String, V>> iterator() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SMapEntrySetIterator<V>( container, map, internalMap );
		}
	}

	@SuppressWarnings("unchecked")
	private Map.Entry<String, ? extends V> toEntry( final Object object ){
		try {
			return (Map.Entry<String, ? extends V>) object;
		} catch ( final Exception e ){
			return null;
		}
	}

	private Map<String, V> toEntries( final Collection<?> objects ){
		final Map<String, V> result = new HashMap<String, V>();
		for( final Object object: objects ){
			final Map.Entry<String, ? extends V> entry = toEntry( object );
			if( entry == null ) continue;
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}

	boolean remove( final Map.Entry<String, ? extends V> target ) {
		if( target != null ) {
			final String targetKey = target.getKey();
			final V targetValue = target.getValue();
			if( internalMap.containsKey( targetKey ) ) {
				if( Objects.equals( internalMap.get(targetKey), targetValue ) ){
					map.remove( targetKey );
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean remove(final Object object) {
		try( final Unlocker unlocker = container.lock() ) {
			return remove( toEntry( object ) );
		}
	}

	@Override
	public boolean removeAll(final Collection<?> objects) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			for( final Object object: objects ){
				result |= remove( object );
			}
			return result;
		}
	}

	@Override
	public boolean retainAll( final Collection<?> collection ) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			final Map<String, V> target = toEntries( collection );
			final Iterator<Map.Entry<String, V>> itr = internalMap.entrySet().iterator();
			while( itr.hasNext() ){
				final Map.Entry<String, V> entry = itr.next();
				final String key = entry.getKey();
				final V value = entry.getValue();

				if( target.containsKey(key) != true || Objects.equals(target.get(key), value) != true ) {
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
			return internalMap.entrySet().toArray();
		}
	}

	@Override
	public <T> T[] toArray(final T[] array) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalMap.entrySet().toArray(array);
		}
	}

	@Override
	public String toString(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalMap.entrySet().toString();
		}
	}

	@Override
	public boolean equals( final Object other ){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalMap.entrySet().equals( other );
		}
	}
}
