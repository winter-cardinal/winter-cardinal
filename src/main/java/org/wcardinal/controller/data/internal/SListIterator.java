/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.List;
import java.util.NoSuchElementException;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

class SListIterator<V> implements java.util.ListIterator<V> {
	final SContainer<?, ?> container;
	final List<V> list;
	final AutoCloseableReentrantLock lock;
	int next;
	int returned;

	SListIterator( final SContainer<?, ?> container, final List<V> list, final int index ){
		this.container = container;
		this.list = list;
		this.lock = container.getLock();
		this.next = index;
		this.returned = -1;
	}

	@Override
	public void add( final V value ) {
		try( final Unlocker unlocker = container.lock() ) {
			try {
				list.add( next, value );
				returned = -1;
				next += 1;
			} catch( final IndexOutOfBoundsException e ) {
				throw new IllegalStateException();
			}
		}
	}

	@Override
	public boolean hasNext() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return next < list.size();
		}
	}

	@Override
	public boolean hasPrevious() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return 0 < next;
		}
	}

	@Override
	public V next() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			try {
				final V result = list.get( next );
				returned = next;
				next += 1;
				return result;
			} catch( final IndexOutOfBoundsException e ) {
				throw new NoSuchElementException();
			}
		}
	}

	@Override
	public int nextIndex() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return next;
		}
	}

	@Override
	public V previous() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			try {
				final int index = next - 1;
				final V result = list.get( next - 1 );
				returned = index;
				next = index;
				return result;
			} catch( final IndexOutOfBoundsException e ) {
				throw new NoSuchElementException();
			}
		}
	}

	@Override
	public int previousIndex() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return next - 1;
		}
	}

	@Override
	public void remove() {
		try( final Unlocker unlocker = container.lock() ) {
			if( returned < 0 ) throw new IllegalStateException();

			try {
				list.remove( returned );
				if( returned < next ) next -= 1;
				returned = -1;
			} catch( final IndexOutOfBoundsException e ) {
				throw new IllegalStateException();
			}
		}
	}

	@Override
	public void set( final V value ) {
		try( final Unlocker unlocker = container.lock() ) {
			if( returned < 0 ) throw new IllegalStateException();
			try {
				list.set( returned, value );
			} catch( final IndexOutOfBoundsException e ) {
				throw new IllegalStateException();
			}
		}
	}
}
