/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

class SQueueValuesIterator<V> implements java.util.Iterator<V> {
	final SQueueValues<V> values;
	Iterator<V[]> iterator;
	V[] current;
	int next;

	SQueueValuesIterator( final SQueueValues<V> values ){
		this.values = values;
		this.iterator = values.data.iterator();
		if( iterator.hasNext() ) {
			this.current = iterator.next();
			this.next = values.offset;
		} else {
			this.current = null;
			this.next = 0;
		}
	}

	@Override
	public boolean hasNext() {
		if( current != null ) {
			if( current != values.last ) {
				return true;
			} else {
				return next < values.psize;
			}
		}
		return false;
	}

	@Override
	public V next() {
		if( current != null ) {
			if( current != values.last ) {
				if( next < current.length ) {
					return current[ next++ ];
				} else {
					current = iterator.next();
					next = 1;
					return current[ 0 ];
				}
			} else {
				if( next < values.psize ) {
					return current[ next++ ];
				}
			}
		}

		throw new NoSuchElementException();
	}

	public void removeHead( final Collection<V> removed ) {
		if( current != null ) {
			boolean isFirst = true;
			iterator = values.data.iterator();
			while( iterator.hasNext() ) {
				final V[] target = iterator.next();
				if( target == current ) break;
				iterator.remove();
				if( isFirst ) {
					isFirst = false;
					values.size -= target.length - values.offset;
					for( int i=values.offset; i<target.length; ++i ) {
						removed.add( target[ i ] );
					}
				} else {
					values.size -= target.length;
					for( final V value: target ) {
						removed.add( value );
					}
				}
			}
			if( values.first == current ) {
				values.size -= next - 1 - values.offset;
			} else {
				values.size -= next - 1;
				values.first = current;
			}
			values.offset = next - 1;
		}
	}

	public void removeTail( final Collection<V> removed ) {
		if( current != null ) {
			while( iterator.hasNext() ) {
				final V[] target = iterator.next();
				iterator.remove();
				if( iterator.hasNext() ) {
					values.size -= target.length;
					for( V value: target ) {
						removed.add( value );
					}
				} else {
					values.size -= values.psize;
					for( int i=0; i<values.psize; ++i ) {
						removed.add( target[ i ] );
					}
				}
			}
			if( values.last == current ) {
				values.size -= values.psize - next + 1;
			} else {
				values.size -= current.length - next + 1;
				values.last = current;
			}
			values.psize = next - 1;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
