/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.NavigableMap;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

class SNavigableMapSubMapValues<V> implements Collection<V> {
	final SNavigableMapContainer<V> container;
	final NavigableMap<String, V> map;
	final NavigableMap<String, V> internalMap;
	final NavigableMap<String, V> internalSubMap;
	final AutoCloseableReentrantLock lock;

	public SNavigableMapSubMapValues( final SNavigableMapContainer<V> container,
			final NavigableMap<String, V> map, final NavigableMap<String, V> internalMap,
			final NavigableMap<String, V> internalSubMap ){
		this.container = container;
		this.map = map;
		this.internalMap = internalMap;
		this.lock = container.getLock();
		this.internalSubMap = internalSubMap;
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
		try( final Unlocker unlocker = container.lock() ) {
			if( container.onRemoveAll( internalSubMap ) ) {
				container.onChange();
				internalSubMap.clear();
			}
		}
	}

	@Override
	public boolean contains( final Object value ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.containsValue( value );
		}
	}

	@Override
	public boolean containsAll( final Collection<?> values ) {
		if( values == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			boolean result = true;
			for( final Object value : values ) {
				if( internalSubMap.containsValue( value ) != true ) {
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
	public Iterator<V> iterator() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapValuesIterator<V>( container, map, internalMap, internalSubMap.entrySet().iterator() );
		}
	}

	@Override
	public boolean remove( final Object o ) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			final Iterator<Map.Entry<String, V>> i = internalSubMap.entrySet().iterator();
			while( i.hasNext() ){
				final Map.Entry<String, V> entry = i.next();
				final String key = entry.getKey();
				final V value = entry.getValue();
				if( Objects.equals( value, o ) ) {
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
	public boolean removeAll( final Collection<?> values ) {
		if( values == null ) throw new NullPointerException();

		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			final Iterator<Map.Entry<String, V>> i = internalSubMap.entrySet().iterator();
			while( i.hasNext() ){
				final Map.Entry<String, V> entry = i.next();
				final String key = entry.getKey();
				final V value = entry.getValue();
				if( values.contains( value ) ) {
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
	public boolean retainAll( final Collection<?> values ) {
		if( values == null ) throw new NullPointerException();

		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			final Iterator<Map.Entry<String, V>> i = internalSubMap.entrySet().iterator();
			while( i.hasNext() ){
				final Map.Entry<String, V> entry = i.next();
				final String key = entry.getKey();
				final V value = entry.getValue();
				if( values.contains( value ) != true ) {
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
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.size();
		}
	}

	@Override
	public Object[] toArray() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.values().toArray();
		}
	}

	@Override
	public <T> T[] toArray( final T[] array ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.values().toArray( array );
		}
	}

	@Override
	public String toString(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.values().toString();
		}
	}

	@Override
	public boolean equals( final Object other ){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.values().equals( other );
		}
	}
}
