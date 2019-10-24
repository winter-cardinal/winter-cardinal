/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedSet;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

class SNavigableMapSubMapKeySet<V> extends AbstractSet<String> implements NavigableSet<String> {
	final SNavigableMapContainer<V> container;
	final NavigableMap<String, V> map;
	final NavigableMap<String, V> internalMap;
	final AutoCloseableReentrantLock lock;
	final NavigableMap<String, V> internalSubMap;

	public SNavigableMapSubMapKeySet( final SNavigableMapContainer<V> container,
			final NavigableMap<String, V> map, final NavigableMap<String, V> internalMap,
			final NavigableMap<String, V> internalSubMap ){
		this.container = container;
		this.map = map;
		this.internalMap = internalMap;
		this.lock = container.getLock();
		this.internalSubMap = internalSubMap;
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
		try( final Unlocker unlocker = container.lock() ) {
			if( container.onRemoveAll( internalSubMap ) ) {
				container.onChange();
				internalSubMap.clear();
			}
		}
	}

	@Override
	public boolean contains( final Object key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.containsKey( key );
		}
	}

	@Override
	public boolean containsAll( final Collection<?> keys ) {
		if( keys == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			boolean result = true;
			for( final Object key : keys ) {
				if( internalSubMap.containsKey( key ) != true ) {
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
	public Iterator<String> iterator() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySetIterator<V>( container, map, internalMap, internalSubMap.entrySet().iterator() );
		}
	}

	@Override
	public boolean remove( final Object o ) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			if( internalSubMap.containsKey( o ) ){
				map.remove( o );
				result = true;
			}
			return result;
		}
	}

	@Override
	public boolean removeAll( final Collection<?> keys ) {
		if( keys == null ) throw new NullPointerException();

		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			for( final Object key: keys ){
				if( internalSubMap.containsKey( key ) ){
					map.remove( key );
					result = true;
				}
			}
			return result;
		}
	}

	@Override
	public boolean retainAll( final Collection<?> keys ) {
		if( keys == null ) throw new NullPointerException();

		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			final Iterator<Map.Entry<String, V>> i = internalSubMap.entrySet().iterator();
			while( i.hasNext() ){
				final String key = i.next().getKey();
				if( keys.contains( key ) != true ) {
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
			return internalSubMap.keySet().toArray();
		}
	}

	@Override
	public <T> T[] toArray( final T[] array ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.keySet().toArray( array );
		}
	}

	@Override
	public Comparator<? super String> comparator() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.comparator();
		}
	}

	@Override
	public String first() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.firstKey();
		}
	}

	@Override
	public String last() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.lastKey();
		}
	}

	@Override
	public String ceiling( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.ceilingKey( key );
		}
	}

	@Override
	public Iterator<String> descendingIterator() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySetIterator<V>( container, map, internalMap, internalSubMap.descendingMap().entrySet().iterator() );
		}
	}

	@Override
	public NavigableSet<String> descendingSet() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( container, map, internalMap, internalSubMap.descendingMap() );
		}
	}

	@Override
	public String floor( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.floorKey( key );
		}
	}

	@Override
	public SortedSet<String> headSet( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( container, map, internalMap, internalSubMap.headMap( key, false ) );
		}
	}

	@Override
	public NavigableSet<String> headSet( final String key, final boolean inclusive ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( container, map, internalMap, internalSubMap.headMap( key, inclusive )  );
		}
	}

	@Override
	public String higher( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.higherKey( key );
		}
	}

	@Override
	public String lower( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.lowerKey( key );
		}
	}

	@Override
	public String pollFirst() {
		try( final Unlocker unlocker = container.lock() ) {
			String result = null;
			if( ! internalSubMap.isEmpty() ) {
				final Map.Entry<String, V> entry = internalSubMap.pollFirstEntry();
				result = entry.getKey();
				container.onRemove( result );
				container.onChange();
			}
			return result;
		}
	}

	@Override
	public String pollLast() {
		try( final Unlocker unlocker = container.lock() ) {
			String result = null;
			if( ! internalSubMap.isEmpty() ) {
				final Map.Entry<String, V> entry = internalSubMap.pollLastEntry();
				result = entry.getKey();
				container.onRemove( result );
				container.onChange();
			}
			return result;
		}
	}

	@Override
	public SortedSet<String> subSet( final String from, final String to ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( container, map, internalMap, internalSubMap.subMap( from, true, to, false ) );
		}
	}

	@Override
	public NavigableSet<String> subSet( final String from, final boolean includeFrom, final String to, final boolean includeTo ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( container, map, internalMap, internalSubMap.subMap( from, includeFrom, to, includeTo ) );
		}
	}

	@Override
	public SortedSet<String> tailSet( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( container, map, internalMap, internalSubMap.tailMap( key, true ) );
		}
	}

	@Override
	public NavigableSet<String> tailSet( final String key, final boolean inclusive ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( container, map, internalMap, internalSubMap.tailMap( key, inclusive ) );
		}
	}

	@Override
	public String toString(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.keySet().toString();
		}
	}

	@Override
	public boolean equals( final Object other ){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.keySet().equals( other );
		}
	}
}
