/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.NavigableMap;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

class SNavigableMapSubMapEntrySet<V> extends SNavigableMapSubMap<V> implements Set<Map.Entry<String, V>> {
	SNavigableMapSubMapEntrySet( final SNavigableMapContainer<V> container,
		final NavigableMap<String, V> map, final NavigableMap<String, V> internalMap,
		final String fromKey, final boolean includeFrom, final String toKey,
		final boolean includeTo, final boolean reverse ){
		super( container, map, internalMap, fromKey, includeFrom, toKey, includeTo, reverse );
	}

	boolean add( final String key, final V value ) {
		if( internalSubMap.containsKey( key ) ) {
			if( Objects.equals( internalSubMap.get( key ), value ) != true ) {
				map.put( key, value );
				return true;
			} else {
				return false;
			}
		} else {
			map.put( key, value );
			return true;
		}
	}

	@Override
	public boolean add( final Map.Entry<String, V> entry ) {
		if( entry == null ) throw new NullPointerException();

		final String key = entry.getKey();
		final V value = entry.getValue();

		rangeCheck( key, true );
		try( final Unlocker unlocker = container.lock() ) {
			return add( key, value );
		}
	}

	@Override
	public boolean addAll( final Collection<? extends Map.Entry<String, V>> map ) {
		if( map == null ) throw new NullPointerException();

		for( final Map.Entry<String, V> entry: map ){
			if( entry == null ) throw new NullPointerException();
			rangeCheck( entry.getKey(), true );
		}

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
		try( final Unlocker unlocker = container.lock() ) {
			if( container.onRemoveAll( internalSubMap ) ) {
				container.onChange();
				internalSubMap.clear();
			}
		}
	}

	boolean contains( final Map.Entry<String, ? extends V> target ) {
		if( target != null ) {
			final String targetKey = target.getKey();
			final V targetValue = target.getValue();
			if( inRange( targetKey, true ) ) {
				final V value = internalMap.get( targetKey );
				return ( Objects.equals(value, targetValue) );
			}
		}
		return false;
	}

	@Override
	public boolean contains( final Object object ) {
		if( object == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return contains( toEntry( object ) );
		}
	}

	@Override
	public boolean containsAll(final Collection<?> objects) {
		if( objects == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			boolean result = true;
			for( final Object object: objects ){
				if( contains( object ) != true ) {
					result = false;
					break;
				}
			}
			return result;
		}
	}

	@Override
	public boolean isEmpty() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.isEmpty();
		}
	}

	@Override
	public Iterator<Map.Entry<String, V>> iterator() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapEntrySetIterator<V>( container, map, internalMap, internalSubMap.entrySet().iterator() );
		}
	}

	boolean remove( final Map.Entry<String, ? extends V> target ) {
		if( target != null ) {
			final String targetKey = target.getKey();
			final V targetValue = target.getValue();
			if( inRange( targetKey, true ) ) {
				if( internalSubMap.containsKey( targetKey ) ) {
					if( Objects.equals(internalSubMap.get( targetKey ), targetValue) ){
						map.remove( targetKey );
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean remove(final Object object) {
		if( object == null ) throw new NullPointerException();

		try( final Unlocker unlocker = container.lock() ) {
			return remove( toEntry( object ) );
		}
	}

	@Override
	public boolean removeAll(final Collection<?> objects) {
		if( objects == null ) throw new NullPointerException();

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
		if( collection == null ) throw new NullPointerException();

		try( final Unlocker unlocker = container.lock() ) {
			final Map<String, V> target = toEntries( collection );

			boolean result = false;
			final Iterator<Map.Entry<String, V>> iterator = internalSubMap.entrySet().iterator();
			while( iterator.hasNext() ){
				final Map.Entry<String, V> entry = iterator.next();
				final String key = entry.getKey();
				final V value = entry.getValue();

				// Skip if exists
				if( target.containsKey( key ) && Objects.equals( target.get( key ), value ) ) continue;

				// Remove
				iterator.remove();
				container.onRemove( key );
				result = true;
			}
			if( result ) container.onChange();
			return result;
		}
	}

	@Override
	public int size() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.size();
		}
	}

	@Override
	public Object[] toArray() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.entrySet().toArray();
		}
	}

	@Override
	public <T> T[] toArray(final T[] array) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.entrySet().toArray(array);
		}
	}

	@Override
	public String toString(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.entrySet().toString();
		}
	}

	@Override
	public boolean equals( final Object other ){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.entrySet().equals( other );
		}
	}
}
