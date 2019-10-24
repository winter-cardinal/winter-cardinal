/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

class SNavigableMapSubMapImpl<V> extends SNavigableMapSubMap<V> implements NavigableMap<String, V> {
	public SNavigableMapSubMapImpl(
			final SNavigableMapContainer<V> container,
			final NavigableMap<String, V> map, final NavigableMap<String, V> internalMap,
			final String fromKey, final boolean includeFrom,
			final String toKey, final boolean includeTo,
			final boolean reverse ){
		super( container, map, internalMap, fromKey, includeFrom, toKey, includeTo, reverse );
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
	public boolean containsKey( final Object key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.containsKey( key );
		}
	}

	@Override
	public boolean containsValue( final Object value ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.containsValue( value );
		}
	}

	@Override
	public V get( final Object key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.get( key );
		}
	}

	@Override
	public boolean isEmpty() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.isEmpty();
		}
	}

	@Override
	public V put( final String key, final V value ) {
		rangeCheck( key, true );
		return map.put(key, value);
	}

	@Override
	public void putAll( final Map<? extends String, ? extends V> mapping ) {
		for( final String key: mapping.keySet() ) rangeCheck( key, true );
		map.putAll( mapping );
	}

	@Override
	public V remove( final Object key ) {
		rangeCheck( key, true );
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return map.remove( key );
		}
	}

	@Override
	public int size() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.size();
		}
	}

	@Override
	public String toString(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.toString();
		}
	}

	@Override
	public boolean equals( final Object other ){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.equals( other );
		}
	}

	@Override
	public Comparator<? super String> comparator() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.comparator();
		}
	}

	@Override
	public Set<Map.Entry<String, V>> entrySet() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapEntrySet<V>( container, map, internalMap, fromKey, includeFrom, toKey, includeTo, reverse );
		}
	}

	@Override
	public String firstKey() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.firstKey();
		}
	}

	@Override
	public SortedMap<String, V> headMap( final String toKey ) {
		rangeCheck( toKey, false );
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return ( reverse ?
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, toKey, false, this.toKey, includeTo, true ) :
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, fromKey, includeFrom, toKey, false, false )
			);
		}
	}

	@Override
	public Set<String> keySet() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( container, map, internalMap, internalSubMap );
		}
	}

	@Override
	public String lastKey() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.lastKey();
		}
	}

	@Override
	public SortedMap<String, V> subMap( final String fromKey, final String toKey ) {
		rangeCheck( fromKey, true );
		rangeCheck( toKey, false );
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return ( reverse ?
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, toKey, false, fromKey, true, true ) :
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, fromKey, true, toKey, false, false )
			);
		}
	}

	@Override
	public SortedMap<String, V> tailMap( final String fromKey ) {
		rangeCheck( fromKey, true );
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return ( reverse ?
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, this.fromKey, includeFrom, fromKey, true, true ) :
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, fromKey, true, toKey, includeTo, false )
			);
		}
	}

	@Override
	public Collection<V> values() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapValues<V>( container, map, internalMap, internalSubMap );
		}
	}

	@Override
	public Map.Entry<String, V> ceilingEntry( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( internalSubMap.ceilingEntry( key ), container, map );
		}
	}

	@Override
	public String ceilingKey( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.ceilingKey( key );
		}
	}

	@Override
	public NavigableSet<String> descendingKeySet() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapImpl<V>( container, map, internalMap, fromKey, includeFrom, toKey, includeTo, !reverse ).navigableKeySet();
		}
	}

	@Override
	public NavigableMap<String, V> descendingMap() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapImpl<V>( container, map, internalMap, fromKey, includeFrom, toKey, includeTo, !reverse );
		}
	}

	@Override
	public Map.Entry<String, V> firstEntry() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( internalSubMap.firstEntry(), container, map );
		}
	}

	@Override
	public Map.Entry<String, V> floorEntry( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( internalSubMap.floorEntry( key ), container, map );
		}
	}

	@Override
	public String floorKey( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.floorKey( key );
		}
	}

	@Override
	public NavigableMap<String, V> headMap( final String toKey, final boolean inclusive ) {
		rangeCheck( toKey, inclusive );
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return ( reverse ?
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, toKey, inclusive, this.toKey, includeTo, true ) :
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, fromKey, includeFrom, toKey, includeTo, false )
			);
		}
	}

	@Override
	public Map.Entry<String, V> higherEntry( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( internalSubMap.higherEntry( key ), container, map );
		}
	}

	@Override
	public String higherKey( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.higherKey( key );
		}
	}

	@Override
	public Map.Entry<String, V> lastEntry() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( internalSubMap.lastEntry(), container, map );
		}
	}

	@Override
	public Map.Entry<String, V> lowerEntry( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( internalSubMap.lowerEntry( key ), container, map );
		}
	}

	@Override
	public String lowerKey( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalSubMap.lowerKey( key );
		}
	}

	@Override
	public NavigableSet<String> navigableKeySet() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( container, map, internalMap, internalSubMap );
		}
	}

	@Override
	public Map.Entry<String, V> pollFirstEntry() {
		try( final Unlocker unlocker = container.lock() ) {
			final Map.Entry<String, V> entry = internalSubMap.pollFirstEntry();
			if( entry != null ) {
				container.onRemove( entry.getKey() );
				container.onChange();
			}
			return entry;
		}
	}

	@Override
	public Map.Entry<String, V> pollLastEntry() {
		try( final Unlocker unlocker = container.lock() ) {
			final Map.Entry<String, V> entry = internalSubMap.pollLastEntry();
			if( entry != null ) {
				container.onRemove( entry.getKey() );
				container.onChange();
			}
			return entry;
		}
	}

	@Override
	public NavigableMap<String, V> subMap( final String fromKey, final boolean includeFrom,
			final String toKey, final boolean includeTo ) {
		rangeCheck( fromKey, includeFrom );
		rangeCheck( toKey, includeTo );
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return ( reverse ?
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, toKey, includeTo, fromKey, includeFrom, true ) :
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, fromKey, includeFrom, toKey, includeTo, false )
			);
		}
	}

	@Override
	public NavigableMap<String, V> tailMap( final String fromKey, final boolean inclusive ) {
		rangeCheck( fromKey, inclusive );
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return ( reverse ?
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, this.fromKey, includeFrom, fromKey, inclusive, true ) :
				new SNavigableMapSubMapImpl<V>( container, map, internalMap, fromKey, inclusive, toKey, includeTo, false )
			);
		}
	}
}
