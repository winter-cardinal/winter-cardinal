/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;

public class SQueueValues<V> implements Queue<V> {
	static final int INITIAL_NSIZE = 8;

	final Deque<V[]> data = new LinkedList<>();
	V[] last, first;
	int offset;	// OFFSET IN THE FIRST PIECE
	int psize;	// SIZE IN THE LAST PIECE
	int size;	// TOTAL SIZE
	int capacity = Integer.MAX_VALUE;

	public SQueueValues(){
		clear();
	}

	public int getCapacity() {
		return this.capacity;
	}

	public void setCapacity( final int capacity ) {
		this.capacity = capacity;
	}

	public static int nextSize( final int base, final int target ) {
		if( target <= base ) return base;
		return ( target >>> 1 ) + 4;
	}

	@SuppressWarnings("unchecked")
	V[] alloc() {
		return (V[]) new Object[ nextSize( INITIAL_NSIZE, size ) ];
	}

	@Override
	public int size() {
		return size;
	}

	boolean add( final V value, final boolean throwWhenFailed ) {
		final int csize = last.length;
		if( psize < csize ) {
			last[ psize ] = value;
			psize += 1;
		} else {
			final V[] newLast = alloc();
			data.add( newLast );
			last = newLast;
			newLast[ 0 ] = value;
			psize = 1;
		}
		size += 1;

		return true;
	}

	V remove( final boolean throwWhenFailed ) {
		if( size <= 0 ) {
			if( throwWhenFailed ) {
				throw new NoSuchElementException();
			} else {
				return null;
			}
		}

		final V result = first[ offset++ ];
		if( getSize( first ) <= offset ) {
			offset = 0;

			if( 1 < data.size() ) {
				data.pop();
				first = data.getFirst();
			} else {
				psize = 0;
			}
		}
		size -= 1;
		return result;
	}

	V getFirst( final boolean throwWhenFailed ) {
		if( size <= 0 ) {
			if( throwWhenFailed ) {
				throw new NoSuchElementException();
			} else {
				return null;
			}
		}

		return first[ offset ];
	}

	@Override
	public void clear() {
		offset = psize = size = 0;
		first = last = alloc();
		data.clear();
		data.add( first );
	}

	int getOffset( final V[] target ) {
		return (target == first ? offset : 0);
	}

	int getSize( final V[] target ) {
		return (target == last ? psize : target.length);
	}

	@Override
	public boolean contains( final Object o ) {
		if( size <= 0 ) return false;

		final Iterator<V[]> iterator = data.iterator();
		while( iterator.hasNext() ) {
			final V[] values = iterator.next();
			for( int i=getOffset( values ), imax=getSize( values ); i<imax; ++i ) {
				if( Objects.equals( o, values[ i ] ) ) return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll( final Collection<?> c ) {
		if( c == null ) throw new NullPointerException();
		if( size <= 0 ) return c.isEmpty();

		for( final Object v: c ) {
			if( contains( v ) != true ) return false;
		}
		return true;
	}

	@Override
	public boolean add( final V value ) {
		return add( value, true );
	}

	@Override
	public boolean addAll( final Collection<? extends V> c ) {
		if( c == null ) throw new NullPointerException();
		for( final V value: c ) {
			add( value, true );
		}
		return !c.isEmpty();
	}

	@Override
	public V remove() {
		return remove( true );
	}

	@Override
	public boolean isEmpty() {
		return size <= 0;
	}

	@Override
	public SQueueValuesIterator<V> iterator() {
		return new SQueueValuesIterator<V>( this );
	}

	@Override
	public boolean remove( final Object o ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll( final Collection<?> c ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll( final Collection<?> c ) {
		throw new UnsupportedOperationException();
	}

	<T> T[] copy( T[] dest ) {
		int position = 0;
		final Iterator<V[]> iterator = data.iterator();
		while( iterator.hasNext() ) {
			final V[] values = iterator.next();
			final int offset = getOffset( values );
			final int size = getSize( values ) - offset;
			System.arraycopy( values, offset, dest, position, size );
			position += size;
		}
		return dest;
	}

	@Override
	public Object[] toArray() {
		return copy( new Object[ size ] );
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray( final T[] a ) {
		final T[] result = ( a.length < size ? (T[]) Array.newInstance(a.getClass().getComponentType(), size) : a );
		copy( result );
		if( size < result.length ) result[ size ] = null;
		return result;
	}

	@Override
	public V element() {
		return getFirst( true );
	}

	@Override
	public boolean offer( final V value ) {
		return add( value, false );
	}

	@Override
	public V peek() {
		return getFirst( false );
	}

	@Override
	public V poll() {
		return remove( false );
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[");

		String delimiter = "";
		final Iterator<V[]> iterator = data.iterator();
		while( iterator.hasNext() ) {
			final V[] values = iterator.next();
			final int offset = getOffset( values );
			final int size = getSize( values );
			for( int i=offset; i<size; ++i ) {
				builder.append( delimiter );
				delimiter = ", ";

				builder.append( values[ i ] );
			}
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals( final Object other ) {
		if( (other instanceof Collection) != true ) return false;

		final Collection<?> otherCollection = (Collection<?>) other;
		if( otherCollection.size() != size() ) return false;

		return equals( otherCollection.iterator() );
	}

	public boolean equals( final Collection<? extends V> other ) {
		if( other == null || other.size() != size() ) return false;
		return equals( other.iterator() );
	}

	private boolean equals( final Iterator<?> otherIterator ) {
		final Iterator<V[]> iterator = data.iterator();
		while( iterator.hasNext() ) {
			final V[] values = iterator.next();
			final int offset = getOffset( values );
			final int size = getSize( values );
			for( int i=offset; i<size; ++i ) {
				final V value = values[ i ];
				final Object otherValue = otherIterator.next();
				if( Objects.equals(value, otherValue) != true ) return false;
			}
		}

		return true;
	}
}
