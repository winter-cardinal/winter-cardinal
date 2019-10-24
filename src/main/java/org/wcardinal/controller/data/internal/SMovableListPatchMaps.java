/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;

import org.wcardinal.controller.data.SMovableList.Move;
import org.wcardinal.controller.data.SList.Update;

public class SMovableListPatchMaps extends SListPatchMaps {
	private SMovableListPatchMaps() {}

	static <V> void add( int index, final V value, final NavigableMap<Integer, V> added, final NavigableMap<Integer, V> updated, final List<Move<V>> newMoved ) {
		addToUpdated( index, 1, updated );
		index = addToNewMoved( index, 1, newMoved );
		addToAdded( index, value, added );
	}

	static <V> void addAll( int index, final Collection<? extends V> values, final NavigableMap<Integer, V> added, final NavigableMap<Integer, V> updated, final List<Move<V>> newMoved ) {
		addToUpdated( index, values.size(), updated );
		index = addToNewMoved( index, values.size(), newMoved );
		addAllToAdded( index, values, added );
	}

	static <V> void remove( int index, final NavigableMap<Integer, V> added, final NavigableSet<Integer> removed, final NavigableMap<Integer, V> updated, final List<Move<V>> newMoved ) {
		removeToUpdated( index, updated );
		index = removeToNewMoved( index, newMoved );
		removeToAddedToRemoved( index, added, removed );
	}

	static <V> void set( final int index, final V value, final NavigableMap<Integer, V> updated ) {
		updateToUpdated( index, value, updated );
	}

	static <V> void move( final int oldIndex, final int newIndex, final NavigableMap<Integer, V> updated, final List<Move<V>> newMoved ) {
		moveToUpdated( oldIndex, newIndex, updated );
		moveToNewMoved( oldIndex, newIndex, null, newMoved );
	}

	static <V> int countNewIndex( final int newIndex, final List<Move<V>> moveds ) {
		int result = 0;

		for( int i=moveds.size()-1; 0<=i; --i ) {
			final Move<V> moved = moveds.get( i );
			final int movedNewIndex = moved.getNewIndex();

			if( newIndex == movedNewIndex ) {
				return -i-1;
			} else if( movedNewIndex < newIndex ){
				result += 1;
			}
		}

		return result;
	};

	static <V> int countOldIndex( final int oldIndex, final List<Move<V>> moveds ) {
		int result = 0;

		for( int i=moveds.size()-1; 0<=i; --i ) {
			final Move<V> moved = moveds.get( i );
			final int movedOldIndex = moved.getOldIndex();

			if( oldIndex == movedOldIndex ) {
				return -i-1;
			} else if( movedOldIndex < oldIndex ){
				result += 1;
			}
		}

		return result;
	}

	static <V> int toOldIndex( final int newIndex, final List<Move<V>> newMoved, final boolean allowDirect ) {
		final int newCount = countNewIndex( newIndex, newMoved );
		if( newCount < 0 ) return (allowDirect ? newMoved.get(-(newCount + 1)).getOldIndex() : newCount);
		final int newPosition = newIndex - newCount;

		for( int i=newIndex; 0<=i; --i ) {
			final int oldCount = countOldIndex( i, newMoved );
			if( oldCount < 0 ) continue;
			final int oldPosition = i - oldCount;
			if( oldPosition == newPosition ) {
				return i;
			} else if( oldPosition < newPosition ) {
				break;
			} else {
				continue;
			}
		}

		for( int i=newIndex+1; true; ++i ) {
			final int oldCount = countOldIndex( i, newMoved );
			if( oldCount < 0 ) continue;
			final int oldPosition = i - oldCount;
			if( oldPosition == newPosition ) {
				return i;
			} else if( oldPosition < newPosition ) {
				continue;
			} else {
				break;
			}
		}

		return newIndex;
	}

	static <V> int toOldIndexToAdd( final int newIndex, final List<Move<V>> newMoved ) {
		for( int i=newIndex; 0<=i; --i ) {
			final int oldIndex = toOldIndex( i, newMoved, false );
			if( 0 <= oldIndex ) return oldIndex + (i == newIndex ? 0 : 1);
		}
		return 0;
	}

	static <V> int removeToNewMoved( final int index, final List<Move<V>> newMoved ) {
		if( newMoved.isEmpty() != true ) {
			final int newIndex = index;
			final int oldIndex = toOldIndex( newIndex, newMoved, true );

			for( int i=newMoved.size()-1; 0<=i; --i ) {
				final Move<V> move = newMoved.get( i );

				if( newIndex == move.getNewIndex() ) {
					newMoved.remove( i );
					continue;
				}

				final int newIndexDelta = ( newIndex < move.getNewIndex() ? 1 : 0 );
				final int oldIndexDelta = ( oldIndex < move.getOldIndex() ? 1 : 0 );
				if( 0 < newIndexDelta || 0 < oldIndexDelta ) {
					newMoved.set( i, new Move<V>( move.getNewIndex() - newIndexDelta, move.getOldIndex() - oldIndexDelta, move.getValue() ));
				}
			}

			return oldIndex;
		} else {
			return index;
		}
	}

	static <V> int addToNewMoved( final int index, final int size, final List<Move<V>> newMoved ) {
		if( newMoved.isEmpty() != true ) {
			final int newIndex = index;
			final int oldIndex = toOldIndexToAdd( newIndex, newMoved );

			for( int i=newMoved.size()-1; 0<=i; --i ) {
				final Move<V> move = newMoved.get( i );

				final int newIndexDelta = ( newIndex <= move.getNewIndex() ? size : 0 );
				final int oldIndexDelta = ( oldIndex <= move.getOldIndex() ? size : 0 );
				if( 0 < newIndexDelta || 0 < oldIndexDelta ) {
					newMoved.set( i, new Move<V>( move.getNewIndex() + newIndexDelta, move.getOldIndex() + oldIndexDelta, move.getValue() ) );
				}
			}

			return oldIndex;
		} else {
			return index;
		}
	}

	static <V> void moveToUpdated( final int oldIndex, final int newIndex, final NavigableMap<Integer, V> updated ) {
		if( updated.isEmpty() != true ) {
			final boolean has = updated.containsKey( oldIndex );
			final V update = (has ? updated.remove( oldIndex ) : null);

			if( oldIndex <= newIndex ) {
				moveKey( updated, updated.subMap(oldIndex, false, newIndex, true), -1 );
			} else {
				moveKey( updated, updated.subMap(newIndex, true, oldIndex, false), +1 );
			}

			if( has ) {
				updated.put(newIndex, update);
			}
		}
	}

	static <V> V moveToUpdated( final int oldIndex, final int newIndex, final V value, final NavigableMap<Integer, Update<V>> updated ) {
		if( updated.isEmpty() != true ) {
			final Update<V> update = updated.remove( oldIndex );

			if( oldIndex <= newIndex ) {
				moveKey( updated, updated.subMap(oldIndex, false, newIndex, true), -1 );
			} else {
				moveKey( updated, updated.subMap(newIndex, true, oldIndex, false), +1 );
			}

			if( update != null ) {
				updated.put(newIndex, update);
				return update.getOldValue();
			}
		}
		return value;
	}

	static <V> void moveToNewMoved( final int oldIndex, final int newIndex, final V value, final List<Move<V>> newMoved ) {
		if( newMoved.isEmpty() ) {
			newMoved.add( 0, new Move<V>( newIndex, oldIndex, value ) );
		} else {
			Move<V> found = null;
			final int oldOldIndex = toOldIndex( oldIndex, newMoved, false );
			if( oldOldIndex < 0 ) {
				final int foundIndex = -(oldOldIndex + 1);
				found = newMoved.remove( foundIndex );
				found = new Move<V>( newIndex, found.getOldIndex(), found.getValue() );
			} else {
				found = new Move<V>( newIndex, oldOldIndex, value );
			}

			if( oldIndex <= newIndex ) {
				for( int i = newMoved.size()-1; 0<=i; --i ) {
					final Move<V> move = newMoved.get( i );
					if( move.getNewIndex() <= oldIndex ) break;
					if( move.getNewIndex() <= newIndex ) {
						newMoved.set( i, new Move<V>( move.getNewIndex() - 1, move.getOldIndex(), move.getValue() ) );
					}
				}
			} else {
				for( int i = newMoved.size()-1; 0<=i; --i ) {
					final Move<V> move = newMoved.get( i );
					if( move.getNewIndex() < newIndex ) break;
					if( move.getNewIndex() < oldIndex ) {
						newMoved.set( i, new Move<V>( move.getNewIndex() + 1, move.getOldIndex(), move.getValue() ) );
					}
				}
			}

			for( int i = newMoved.size()-1; 0<=i; --i ) {
				final Move<V> move = newMoved.get( i );
				if( newIndex < move.getNewIndex() ) continue;
				newMoved.add( i+1, found );
				return;
			}
			newMoved.add( 0, found );
		}
	}

	static <V> int findNewIndex( final int targetCount, final int[] sortedOldIndices ) {
		int count = 0;
		int previous = -1;
		for( final int oldIndex: sortedOldIndices ) {
			final int diff = oldIndex - previous - 1;
			if( count <= targetCount && targetCount <= count + diff ) {
				return previous + 1 + (targetCount - count);
			} else {
				count += diff;
				previous = oldIndex;
			}
		}
		return previous + 1 + (targetCount - count);
	}

	static <V> int findNewIndex( final int newIndex, final int oldIndex, final int index, final int[] sortedOldIndices, final List<Move<V>> moved ) {
		int result = findNewIndex( newIndex - index, sortedOldIndices );

		for( int i=result; true; ++i ) {
			if( Arrays.binarySearch( sortedOldIndices, i ) < 0 ) {
				result = i;
				break;
			}
		}

		for(int i=0; i<moved.size(); ++i ) {
			final Move<V> move = moved.get( i );
			final int otherNewIndex = move.getNewIndex();
			final int otherOldIndex = move.getOldIndex();
			if( otherNewIndex <= otherOldIndex ) {
				if( otherNewIndex <= result && result < otherOldIndex ) result += 1;
			} else {
				if( otherOldIndex < result && result <= otherNewIndex ) result -= 1;
			}
		}

		if( oldIndex < result ) {
			result -= 1;
		}

		return result;
	};

	static <V> int findOldIndex( final int targetOldIndex, final List<Move<V>> moved ) {
		int result = targetOldIndex;
		for( final Move<V> move: moved ) {
			final int newIndex = move.getNewIndex();
			final int oldIndex = move.getOldIndex();

			if( newIndex <= oldIndex ) {
				if( newIndex <= result && result < oldIndex ) result += 1;
			} else {
				if( oldIndex < result && result <= newIndex ) result -= 1;
			}
		}
		return result;
	}

	static <V> List<Move<V>> toMoved( final List<Move<V>> newMoved ) {
		if( newMoved.size() <= 1 ) return newMoved;

		final List<Move<V>> result = new ArrayList<>();

		final int[] sortedOldIndices = new int[ newMoved.size() ];
		for( int i=0; i<newMoved.size(); ++i ) {
			sortedOldIndices[ i ] = newMoved.get( i ).getOldIndex();
		}
		Arrays.sort( sortedOldIndices );

		for( int i=0; i<newMoved.size(); ++i ) {
			final Move<V> b = newMoved.get( i );
			final int oldIndex = findOldIndex( b.getOldIndex(), result );
			final int newIndex = findNewIndex( b.getNewIndex(), oldIndex, i, sortedOldIndices, result );
			if( newIndex != oldIndex ) {
				result.add(new Move<V>( newIndex, oldIndex, null ));
			}
		}

		return result;
	}

	public static <V> void apply( final NavigableMap<Integer, V> cadded, final NavigableSet<Integer> cremoved,
			final NavigableMap<Integer, V> cupdated, final List<Move<V>> cnewMoved, final List<V> list,
			final NavigableMap<Integer, V> padded, final NavigableMap<Integer, V> premoved,
			final NavigableMap<Integer, Update<V>> pupdated, final List<Move<V>> pnewMoved ) {
		// Remove
		for( int index: cremoved.descendingSet() ) {
			V value = list.remove( index );

			value = removeToUpdated( index, value, pupdated );
			index = removeToNewMoved( index, pnewMoved );
			removeToAddedToRemoved( index, value, padded, premoved );
		}

		// Add
		for( final Map.Entry<Integer, V> entry: cadded.entrySet() ) {
			int index = entry.getKey();
			V value = entry.getValue();

			list.add( index, value );

			addToUpdated( index, 1, pupdated );
			index = addToNewMoved( index, 1, pnewMoved );
			addToAdded( index, value, padded );
		}

		// New move
		for( final Move<V> move: toMoved( cnewMoved ) ) {
			final int newIndex = move.getNewIndex();
			final int oldIndex = move.getOldIndex();

			V value = list.remove( oldIndex );
			list.add( newIndex, value );

			value = moveToUpdated( oldIndex, newIndex, value, pupdated );
			moveToNewMoved( oldIndex, newIndex, value, pnewMoved );
		}

		// Update
		for( final Map.Entry<Integer, V> entry: cupdated.entrySet() ) {
			final int index = entry.getKey();
			final V newValue = entry.getValue();

			final V oldValue = list.set( index, newValue );

			updateToUpdated( index, newValue, oldValue, pupdated );
		}
	}

}
