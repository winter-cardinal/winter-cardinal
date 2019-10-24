/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import java.util.Collection;
import java.util.Queue;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * Represents a read-only queue and is more efficient for data streams
 * compared to the {@link org.wcardinal.controller.data.SList}.
 * The default capacity is {@link Integer#MAX_VALUE}.
 * The following are not supported for efficiency reasons:
 *
 * * {@link Queue#remove(Object)},
 * * {@link Queue#removeAll(java.util.Collection)},
 * * {@link Queue#retainAll(java.util.Collection)}.
 *
 * @param <T> data type
 */
public abstract class SROQueue<T> implements SLockable, SVariable, Queue<T> {
	@ThreadSafe
	public int capacity( int capacity ) {
		return setCapacity( capacity );
	}

	@ThreadSafe
	public int capacity() {
		return getCapacity();
	}

	/**
	 * Sets to the specified capacity.
	 * Older elements are removed to fit to the specified capacity
	 * if the number of elements this queue has is larger than the specified capacity.
	 *
	 * @param capacity the new capacity
	 * @return the previous capacity
	 * @throws IllegalArgumentException if the specified capacity is negative
	 */
	@ThreadSafe
	public abstract int setCapacity( int capacity );

	/**
	 * Returns the capacity.
	 *
	 * @return the capacity
	 */
	@ThreadSafe
	public abstract int getCapacity();

	/**
	 * Clear this queue and then appends the specified element to this queue.
	 *
	 * @param element element to be appended to this queue
	 * @return true
	 * @throws NullPointerException if this queue does not permit null, and the specified element is null
	 */
	@ThreadSafe
	public abstract boolean clearAndAdd( T element );

	/**
	 * Clear this queue and then appends the specified element to this queue without raising an exception.
	 *
	 * @param element element to be appended to this queue
	 * @return true if the specified element is successfully appended
	 * @throws NullPointerException if this queue does not permit null, and the specified element is null
	 */
	@ThreadSafe
	public abstract boolean clearAndOffer( T element );

	/**
	 * Clear this queue and then appends the specified elements to this queue.
	 *
	 * @param elements elements to be appended to this queue
	 * @return true if this queue is changed as a result of the call
	 * @throws NullPointerException if the specified collection is null, or if this queue does not permit null, and the specified collection contains null
	 */
	@ThreadSafe
	public abstract boolean clearAndAddAll( Collection<? extends T> elements );

	/**
	 * Clear this queue and then appends the specified elements to this queue.
	 *
	 * @param elements elements to be appended to this queue
	 * @return true if this queue is changed as a result of the call
	 * @throws NullPointerException if the specified collection is null, or if this queue does not permit null, and the specified collection contains null
	 */
	@ThreadSafe
	public abstract boolean clearAndOfferAll( Collection<? extends T> elements );

	/**
	 * Returns true if the specified target is equal to this.
	 *
	 * @param other the target to be compared
	 * @return true if the specified target is equal to this
	 */
	@ThreadSafe
	public abstract boolean equals( Collection<? extends T> other );
}
