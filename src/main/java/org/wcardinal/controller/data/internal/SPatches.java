/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public abstract class SPatches<V, P extends SPatch> {
	final NavigableMap<Long, P> revisionToPatch;

	public SPatches() {
		revisionToPatch = new TreeMap<>();
	}

	protected P getOrCreate( final long revision ) {
		final P result;
		final Iterator<Map.Entry<Long, P>> iterator = revisionToPatch.descendingMap().entrySet().iterator();
		if( iterator.hasNext() ) {
			final Map.Entry<Long, P> lastEntry = iterator.next();
			final P lastValue = lastEntry.getValue();
			if( lastValue != null ) {
				result = lastValue;
				if( lastEntry.getKey() != revision ) {
					if( iterator.hasNext() ) {
						final Map.Entry<Long, P> secondLastEntry = iterator.next();
						if( secondLastEntry.getValue() != null ) {
							revisionToPatch.pollLastEntry();
						} else {
							lastEntry.setValue( getPatchMapDummy() );
						}
					} else {
						lastEntry.setValue( getPatchMapDummy() );
					}
					revisionToPatch.put( revision, result );
				}
			} else {
				result = newPatchMap();
				revisionToPatch.put( revision, result );
			}
		} else {
			result = newPatchMap();
			revisionToPatch.put( revision, result );
		}
		return result;
	}

	abstract P newPatchMap();
	abstract P getPatchMapDummy();
	abstract P newPatchReset();
	abstract P newPatchReset( V value );
	abstract SPatchesPacked<V, P> newPatchesPacked( final long authorizedRevision, final long revision, P patchReset );
	abstract SPatchesPacked<V, P> newPatchesPacked( final long authorizedRevision, final long revision, NavigableMap<Long, P> revisionToPatch );

	public void clear( final long revision ) {
		revisionToPatch.clear();
		revisionToPatch.put( revision, newPatchReset() );
	}

	public void compact( final long authorizedRevision ) {
		revisionToPatch.headMap( authorizedRevision, false ).clear();
	}

	public void apply( final long revision, final SPatchesPacked<V, P> other ) {
		if( other.getReset() != null ) {
			clear( revision );
		} else {
			this.revisionToPatch.putAll(
				other.getRevisionToMap().tailMap( Math.max( revision, other.getStartRevision() ) )
			);
		}
	}

	public void reset( final long revision ) {
		clear( revision );
	}

	public void resetIfTooHeavy( final int weightCriteria ) {
		if( revisionToPatch.isEmpty() ) return;

		int weight = 0;
		for( final P patch: revisionToPatch.values() ) {
			if( patch != null ) {
				weight += patch.getWeight();
			}
		}
		if( weight <= weightCriteria ) return;

		clear( revisionToPatch.lastKey() );
	}

	protected boolean containsReset() {
		for( final P patch: revisionToPatch.values() ) {
			if( patch != null && patch.isReset() ) return true;
		}
		return false;
	}

	protected boolean containsNoPatchesOf( final long authorizedRevision ) {
		if( revisionToPatch.isEmpty() != true ) {
			if( 0 <= authorizedRevision ) {
				return authorizedRevision < this.revisionToPatch.firstKey();
			} else {
				return 0 < this.revisionToPatch.firstKey();
			}
		}
		return false;
	}

	abstract int size( final V value );

	public SPatchesPacked<V, P> pack( final long authorizedRevision, final boolean forceReset, final long revision, final V value, final boolean isSoft ) {
		final int size = size( value );
		resetIfTooHeavy( size );

		if( isSoft || forceReset || size <= 0 || containsNoPatchesOf( authorizedRevision ) || containsReset() ) {
			for( final Map.Entry<Long, P> entry: revisionToPatch.headMap( revision ).entrySet() ) {
				if( entry.getValue() != null ) {
					entry.setValue( null );
				}
			}
			return newPatchesPacked( authorizedRevision, revision, newPatchReset( value ) );
		} else {
			boolean isEmpty = true;
			long firstRevision = 0;
			final P dummy = getPatchMapDummy();
			final NavigableMap<Long, P> revisionToPatchPacked = new TreeMap<>();
			for( final Map.Entry<Long, P> entry: revisionToPatch.subMap( authorizedRevision, revision ).entrySet() ) {
				final P patch = entry.getValue();
				if( patch != null ) {
					entry.setValue( null );
					if( patch != dummy ) {
						revisionToPatchPacked.put( entry.getKey(), patch );
					}
					if( isEmpty ) {
						isEmpty = false;
						firstRevision = entry.getKey();
					}
				}
			}
			if( isEmpty ) {
				return newPatchesPacked( revision, revision, revisionToPatchPacked );
			} else {
				return newPatchesPacked( firstRevision, revision, revisionToPatchPacked );
			}
		}
	}

	public boolean isEmpty() {
		return revisionToPatch.isEmpty();
	}

	public void clear() {
		revisionToPatch.clear();
	}
}
