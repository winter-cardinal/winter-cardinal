/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

public class SListSubList<V> implements List<V> {
	final SContainer<?, ?> container;
	final List<V> list;
	final List<V> internalList;
	final AutoCloseableReentrantLock lock;
	final int fromIndex;
	int size;

	public SListSubList( final SContainer<?, ?> container, final List<V> list, final List<V> internalList, final int fromIndex, final int toIndex ){
		checkRange( fromIndex, toIndex, internalList.size() );
		this.container = container;
		this.list = list;
		this.internalList = internalList;
		this.lock = container.getLock();
		this.fromIndex = fromIndex;
		this.size = toIndex - fromIndex;
	}

	void checkIndex( final int index, final boolean includeTo ) {
		if( index < 0 || ( includeTo ? size < index : size <= index ) ) throw new IndexOutOfBoundsException( "index: "+index+" size:"+size );
	}

	void checkRange( final int fromIndex, final int toIndex, final int size ) {
		if( fromIndex < 0 ) {
			throw new IndexOutOfBoundsException( "Out-of-bounds fromIndex: " + fromIndex );
		}

		if( size < toIndex) {
			throw new IndexOutOfBoundsException( "Out-of-bounds toIndex: " + toIndex );
		}

		if( toIndex < fromIndex ) {
			throw new IllegalArgumentException( "Illegal range of [" + fromIndex + ", " + toIndex + ")" );
		}
	}

	@Override
	public boolean add( final V value ) {
		try( final Unlocker unlocker = container.lock() ) {
			list.add( fromIndex + size, value );
			size += 1;
			return true;
		}
	}

	@Override
	public void add( final int index, final V value ) {
		try( final Unlocker unlocker = container.lock() ) {
			checkIndex( index, true );
			list.add( fromIndex + index, value );
			size += 1;
		}
	}

	@Override
	public boolean addAll( final Collection<? extends V> values ) {
		try( final Unlocker unlocker = container.lock() ) {
			final int size = values.size();
			final boolean result = list.addAll( fromIndex+this.size, values );
			if( result ) this.size += size;
			return result;
		}
	}

	@Override
	public boolean addAll( final int index, final Collection<? extends V> values ) {
		if( values == null ) throw new NullPointerException();
		try( final Unlocker unlocker = container.lock() ) {
			checkIndex( index, true );
			final int size = values.size();
			final boolean result = list.addAll( fromIndex+index, values );
			if( result ) this.size += size;
			return result;
		}
	}

	@Override
	public void clear() {
		try( final Unlocker unlocker = container.lock() ) {
			for( int i=size-1; 0<=i; --i ) {
				list.remove( fromIndex+i );
			}
			size = 0;
		}
	}

	@Override
	public boolean contains( final Object target ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			boolean result = false;
			for( int i=0; i<size; ++i ) {
				if( Objects.equals( target, internalList.get( fromIndex+i ) ) ) {
					result = true;
					break;
				}
			}
			return result;
		}
	}

	@Override
	public boolean containsAll( final Collection<?> targets ) {
		if( targets == null ) throw new NullPointerException();
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			boolean result = true;
			for( final Object target: targets ) {
				if( contains( target ) != true ) {
					result = false;
					break;
				}
			}
			return result;
		}
	}

	@Override
	public V get( final int index ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			checkIndex( index, false );
			return internalList.get( fromIndex + index );
		}
	}

	@Override
	public int indexOf( final Object target ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			int result = -1;
			for( int i=0; i<size; ++i ) {
				if( Objects.equals( target, internalList.get( fromIndex+i ) ) ) {
					result = i;
					break;
				}
			}
			return result;
		}
	}

	@Override
	public int lastIndexOf( final Object target ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			int result = -1;
			for( int i=size-1; 0<=i; --i ) {
				if( Objects.equals( target, internalList.get( fromIndex+i ) ) ) {
					result = i;
					break;
				}
			}
			return result;
		}
	}

	@Override
	public boolean isEmpty() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return size <= 0;
		}
	}

	@Override
	public Iterator<V> iterator() {
		return listIterator();
	}

	@Override
	public java.util.ListIterator<V> listIterator() {
		return listIterator( 0 );
	}

	@Override
	public java.util.ListIterator<V> listIterator( final int index ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			checkIndex( index, true );
			return new SListIterator<V>( container, this, fromIndex+index );
		}
	}

	@Override
	public boolean remove( final Object target ) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			for( int i=0; i<size; ++i ) {
				if( Objects.equals( target, internalList.get( fromIndex+i ) ) ) {
					list.remove( fromIndex+i );
					size -= 1;
					result = true;
					break;
				}
			}
			return result;
		}
	}

	@Override
	public V remove( final int index ) {
		try( final Unlocker unlocker = container.lock() ) {
			checkIndex( index, false );
			final V result = list.remove( fromIndex+index );
			size -= 1;
			return result;
		}
	}

	@Override
	public boolean removeAll( final Collection<?> targets ) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			for( int i=size-1; 0<=i; --i ) {
				if( targets.contains( internalList.get( i ) ) ) {
					list.remove( fromIndex+i );
					size -= 1;
					result = true;
				}
			}
			return result;
		}
	}

	@Override
	public boolean retainAll( final Collection<?> targets ) {
		try( final Unlocker unlocker = container.lock() ) {
			boolean result = false;
			for( int i=size-1; 0<=i; --i ) {
				if( targets.contains( internalList.get( fromIndex+i ) ) != true ) {
					list.remove( fromIndex+i );
					size -= 1;
					result = true;
				}
			}
			return result;
		}
	}

	@Override
	public V set( final int index, final V value ) {
		try( final Unlocker unlocker = container.lock() ) {
			checkIndex( index, false );
			return list.set( fromIndex + index, value );
		}
	}

	@Override
	public int size() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return size;
		}
	}

	@Override
	public List<V> subList( final int fromIndex, final int toIndex ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			checkRange( fromIndex, toIndex, size );
			return new SListSubList<V>( container, list, internalList, this.fromIndex+fromIndex, this.fromIndex+toIndex );
		}
	}

	@Override
	public Object[] toArray() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			final Object[] result = new Object[ size ];
			for( int i=0; i<result.length; ++i ) {
				result[ i ] = internalList.get( fromIndex + i );
			}
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> U[] toArray( final U[] array ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			final U[] result = ( size <= array.length ? array :
				(U[]) Array.newInstance(array.getClass().getComponentType(), size)
			);

			for( int i=0; i<size; ++i ) {
				result[ i ] = (U) internalList.get( fromIndex + i );
			}

			if( size < result.length ) {
				result[ size ] = null;
			}

			return result;
		}
	}

	@Override
	public String toString(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append( "[" );
			String delimiter = "";
			for( int i=0; i<size; ++i ) {
				stringBuilder.append( delimiter );
				delimiter = ", ";
				stringBuilder.append( internalList.get( fromIndex + i ) );
			}
			stringBuilder.append( "]" );
			return stringBuilder.toString();
		}
	}

	@Override
	public boolean equals( final Object other ){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			boolean result = false;
			if( other == this ) {
				result = true;
			} else if( other instanceof List ) {
				final List<?> otherList = (List<?>) other;
				if( size == otherList.size() ) {
					final Iterator<?> otherIterator = otherList.iterator();
					for( int i=0; i<size; ++i ) {
						if( Objects.equals( internalList.get( i ), otherIterator.next() ) != true ) {
							break;
						}
					}
				}
				result = true;
			}
			return result;
		}
	}
}
