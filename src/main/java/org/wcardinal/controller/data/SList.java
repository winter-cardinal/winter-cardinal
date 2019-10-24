/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import java.util.Collection;
import java.util.List;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * Represents the list data.
 * The class {@code V} can be any class.
 * However, for synchronization, the class {@code V} must be serializable
 * to, and deserializable from JSON by the Jackson.
 *
 * @param <V> element type
 */
public abstract class SList<V> implements SLockable, SVariable, List<V> {
	public static class Update<V> {
		final V newValue;
		final V oldValue;

		public Update(){
			this.newValue = null;
			this.oldValue = null;
		}

		public Update( final V newValue, final V oldValue ) {
			this.newValue = newValue;
			this.oldValue = oldValue;
		}

		public V getNewValue(){
			return newValue;
		}

		public V getOldValue(){
			return oldValue;
		}

		@Override
		public String toString() {
			return "{newValue: " + newValue + ", oldValue: " + oldValue + "}";
		}
	}

	/**
	 * Replaces the existing elements with the specified elements
	 * if the existing elements are not equal to the specified elements.
	 * For testing the equality of elements, {@code V#equals(Object)} is used.
	 *
	 * @param elements new elements
	 * @throws NullPointerException if the specified iterable is null
	 */
	public abstract void replace( final Iterable<? extends V> elements );

	/**
	 * Marks the element of the specified index as updated.
	 *
	 * @param index index of the element to be marked as updated
	 */
	@ThreadSafe
	public abstract void toDirty( final int index );

	/**
	 * Marks all the elements as updated.
	 */
	@ThreadSafe
	public abstract void toDirty();

	/**
	 * Clear this list and then appends the specified element to this list.
	 *
	 * @param element element to be appended to this list
	 * @return true
	 * @throws NullPointerException if this list does not permit null value, and the specified element is null
	 */
	@ThreadSafe
	public abstract boolean clearAndAdd( final V element );

	/**
	 * Clear this list and then appends the specified elements to this list.
	 *
	 * @param elements elements to be appended to this list
	 * @return true if this list is changed as a result of the call
	 * @throws NullPointerException if the specified collection is null, or if this list does not permit null value, and the specified collection contains null values
	 */
	@ThreadSafe
	public abstract boolean clearAndAddAll( final Collection<? extends V> elements );

	/**
	 * Resets the specified index and returns the value at the specified index.
	 *
	 * @param index the index to be reset
	 * @return the value at the specified index
	 * @throws IndexOutOfBoundsException if the specified index is out of range ({@code index < 0 || size() <= index })
	 */
	@ThreadSafe
	public abstract V reset( int index );
}
