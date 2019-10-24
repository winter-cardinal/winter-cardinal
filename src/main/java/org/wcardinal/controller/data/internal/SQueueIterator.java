/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Iterator;
import java.util.Queue;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;

class SQueueIterator<V> implements java.util.Iterator<V> {
	final Iterator<V> internalIterator;
	final AutoCloseableReentrantLock lock;

	SQueueIterator( final SContainer<?, ?> container, final Queue<V> queue, final Queue<V> internalQueue ){
		this.lock = container.getLock();
		this.internalIterator = internalQueue.iterator();
	}

	@Override
	public boolean hasNext() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalIterator.hasNext();
		}
	}

	@Override
	public V next() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return internalIterator.next();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
