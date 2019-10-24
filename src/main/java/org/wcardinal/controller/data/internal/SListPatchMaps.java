/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;

import org.wcardinal.controller.data.SList.Update;

public class SListPatchMaps {
	SListPatchMaps() {}

	static <V> void add( int index, final V value, final NavigableMap<Integer, V> added, final NavigableMap<Integer, V> updated ) {
		addToUpdated( index, 1, updated );
		addToAdded( index, value, added );
	}

	static <V> void addAll( int index, final Collection<? extends V> values, final NavigableMap<Integer, V> added, final NavigableMap<Integer, V> updated ) {
		addToUpdated( index, values.size(), updated );
		addAllToAdded( index, values, added );
	}

	static <V> void remove( int index, final NavigableMap<Integer, V> added, final NavigableSet<Integer> removed, final NavigableMap<Integer, V> updated ) {
		removeToUpdated( index, updated );
		removeToAddedToRemoved( index, added, removed );
	}

	static <V> void set( final int index, final V value, final NavigableMap<Integer, V> updated ) {
		updateToUpdated( index, value, updated );
	}

	public static <V> void apply( final NavigableMap<Integer, V> cadded, final NavigableSet<Integer> cremoved,
			final NavigableMap<Integer, V> cupdated, final List<V> list,
			final NavigableMap<Integer, V> padded, final NavigableMap<Integer, V> premoved,
			final NavigableMap<Integer, Update<V>> pupdated ) {
		// Remove
		for( int index: cremoved.descendingSet() ) {
			V value = list.remove( index );

			value = removeToUpdated( index, value, pupdated );
			removeToAddedToRemoved( index, value, padded, premoved );
		}

		// Add
		for( final Map.Entry<Integer, V> entry: cadded.entrySet() ) {
			int index = entry.getKey();
			V value = entry.getValue();

			list.add( index, value );

			addToUpdated( index, 1, pupdated );
			addToAdded( index, value, padded );
		}

		// Update
		for( final Map.Entry<Integer, V> entry: cupdated.entrySet() ) {
			final int index = entry.getKey();
			final V newValue = entry.getValue();

			final V oldValue = list.set( index, newValue );

			updateToUpdated( index, newValue, oldValue, pupdated );
		}
	}

	static <V> void removeToUpdated( final int index, final NavigableMap<Integer, V> updated ) {
		if( updated.isEmpty() != true ) {
			updated.remove( index );
			moveKey( updated, updated.tailMap(index), -1 );
		}
	}

	static <V> V removeToUpdated( final int index, final V value, final NavigableMap<Integer, Update<V>> updated ) {
		if( updated.isEmpty() != true ) {
			final Update<V> update = updated.remove( index );
			moveKey( updated, updated.tailMap(index), -1 );
			if( update != null ) {
				return update.getOldValue();
			}
		}
		return value;
	}

	static <V> void addToUpdated( final int index, final int size, final NavigableMap<Integer, V> updated ) {
		if( updated.isEmpty() != true ) {
			moveKey( updated, updated.tailMap(index), size );
		}
	}

	static <V> void updateToUpdated( final int index, final V newValue, final NavigableMap<Integer, V> updated ) {
		updated.put( index, newValue );
	}

	static <V> void updateToUpdated( final int index, final V newValue, final V oldValue, final NavigableMap<Integer, Update<V>> updated ) {
		final Update<V> update = updated.get( index );
		if( update != null ) {
			updated.put( index, new Update<V>( newValue, update.getOldValue() ));
		} else {
			updated.put( index, new Update<V>( newValue, oldValue ) );
		}
	}

	static <V> void moveKey( final Map<Integer, V> map, final Map<Integer, V> target, final int delta ) {
		final Map<Integer, V> movedTarget = new HashMap<>();
		for( Map.Entry<Integer, V> entry: target.entrySet() ) {
			movedTarget.put( entry.getKey() + delta, entry.getValue() );
		}
		target.clear();
		map.putAll( movedTarget );
	}

	static <V> void addToAdded( final int index, final V value, final NavigableMap<Integer, V> added ) {
		moveKey( added, added.tailMap( index ), 1 );
		added.put( index, value );
	}

	static <V> void addAllToAdded( final int index, final Collection<? extends V> values, final NavigableMap<Integer, V> added ) {
		moveKey( added, added.tailMap( index ), values.size() );

		int i = 0;
		for( final V value: values ) {
			added.put( index+i, value );
			i += 1;
		}
	}

	static <V> void removeToAddedToRemoved( final int index, final V value, final NavigableMap<Integer, V> added, final NavigableMap<Integer, V> removed ) {
		if( added.containsKey( index ) ) {
			added.remove( index );
			moveKey( added, added.tailMap( index ), -1 );
		} else {
			moveKey( added, added.tailMap( index ), -1 );
			removeToRemoved( index - added.headMap( index ).size(), value, removed );
		}
	}

	static <V> void removeToAddedToRemoved( final int index, final NavigableMap<Integer, V> added, final Collection<Integer> removed ) {
		if( added.containsKey( index ) ) {
			added.remove( index );
			moveKey( added, added.tailMap( index ), -1 );
		} else {
			moveKey( added, added.tailMap( index ), -1 );
			removeToRemoved( index - added.headMap( index ).size(), removed );
		}
	}

	static <V> void removeToRemoved( final int index, final Collection<Integer> removed ) {
		removed.add( findRemoveIndex( index, removed ) );
	}

	static <V> void removeToRemoved( final int index, final V value, final Map<Integer, V> removed ) {
		removed.put( findRemoveIndex( index, removed.keySet() ), value );
	}

	static <V> int findRemoveIndex( final int index, final Collection<Integer> removedIndices ) {
		int result = index;
		for( final int removedIndex: removedIndices ) {
			if( result < removedIndex ) break;
			result += 1;
		}
		return result;
	}
}
