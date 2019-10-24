/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;

class SNavigableMapSubMap<V> {
	final SNavigableMapContainer<V> container;
	final NavigableMap<String, V> map;
	final NavigableMap<String, V> internalMap;
	final AutoCloseableReentrantLock lock;

	final String fromKey;
	final String toKey;
	final boolean includeFrom;
	final boolean includeTo;
	final boolean reverse;
	final NavigableMap<String, V> internalSubMap;

	public SNavigableMapSubMap( final SNavigableMapContainer<V> container,
			final NavigableMap<String, V> map, final NavigableMap<String, V> internalMap,
			final String fromKey, final boolean includeFrom,
			final String toKey, final boolean includeTo, final boolean reverse ){
		this.container = container;
		this.map = map;
		this.internalMap = internalMap;
		this.lock = container.getLock();

		this.fromKey = fromKey;
		this.toKey = toKey;
		this.includeFrom = includeFrom;
		this.includeTo = includeTo;
		this.reverse = reverse;

		if( fromKey != null ) {
			if( toKey != null ) {
				if( reverse ) {
					internalSubMap = internalMap.subMap(fromKey, includeFrom, toKey, includeTo).descendingMap();
				} else {
					internalSubMap = internalMap.subMap(fromKey, includeFrom, toKey, includeTo);
				}
			} else {
				if( reverse ) {
					internalSubMap = internalMap.tailMap(fromKey, includeFrom).descendingMap();
				} else {
					internalSubMap = internalMap.tailMap(fromKey, includeFrom);
				}
			}
		} else {
			if( toKey != null ) {
				if( reverse ) {
					internalSubMap = internalMap.headMap(toKey, includeTo).descendingMap();
				} else {
					internalSubMap = internalMap.headMap(toKey, includeTo);
				}
			} else {
				if( reverse ) {
					internalSubMap = internalMap.descendingMap();
				} else {
					internalSubMap = internalMap;
				}
			}
		}
	}

	final boolean tooLow( final Object key, final boolean include ) {
		if ( fromKey != null ) {
			if( key instanceof String ) {
				int c = internalMap.comparator().compare((String)key, fromKey);
				if ( c < 0 || (include && includeFrom != true && c==0) ) return true;
			}
		}
		return false;
	}

	final boolean tooHigh( final Object key, final boolean include ) {
		if ( toKey != null ) {
			if( key instanceof String ) {
				int c = internalMap.comparator().compare((String)key, toKey);
				if ( 0 < c || (include && includeTo != true && c == 0) ) return true;
			}
		}
		return false;
	}

	final boolean inRange( final Object key, final boolean include ) {
		return !tooLow(key, include) && !tooHigh(key, include);
	}

	final void rangeCheck( final Object key, final boolean include ){
		if( inRange( key, include ) != true ) {
			throw new IllegalArgumentException( "Key out of range" );
		}
	}

	@SuppressWarnings("unchecked")
	Map.Entry<String, ? extends V> toEntry( final Object object ){
		try {
			return (Map.Entry<String, ? extends V>) object;
		} catch ( final Exception e ){
			return null;
		}
	}

	Map<String, V> toEntries( final Collection<?> objects ){
		final Map<String, V> result = new HashMap<String, V>();
		for( final Object object: objects ){
			final Map.Entry<String, ? extends V> entry = toEntry( object );
			if( entry == null || inRange( entry.getKey(), true ) != true ) continue;
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
}
