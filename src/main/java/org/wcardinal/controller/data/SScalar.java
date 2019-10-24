/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import java.util.Collection;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * Represents the scalar data.
 * The scalar data this class has is synchronized with the one on browsers.
 *
 * @param <T> the data class
 */
public interface SScalar<T> extends SLockable, SVariable {
	/**
	 * Returns the current value.
	 *
	 * @return the current value
	 */
	@ThreadSafe
	T get();

	/**
	 * Sets to the specified value.
	 *
	 * @param value the new value
	 * @return the previous value
	 */
	@ThreadSafe
	T set(final T value);

	/**
	 * Resets to the current value.
	 *
	 * @return the current value
	 */
	@ThreadSafe
	T reset();

	/**
	 * Returns the current value.
	 *
	 * @return the current value
	 */
	@ThreadSafe
	T getValue();

	/**
	 * Sets to the specified value.
	 *
	 * @param value the new value
	 * @return the previous value
	 */
	@ThreadSafe
	T setValue(final T value);

	/**
	 * Resets to the current value.
	 *
	 * @return the current value
	 */
	@ThreadSafe
	T resetValue();

	/**
	 * Atomically sets to the 'update' if the 'expected' is equal to the current value.
	 *
	 * @param expected the expected value
	 * @param update the new value
	 * @return true if the 'expected' is equal to the current value
	 */
	@ThreadSafe
	boolean compareAndSet(final T expected, final T update);

	/**
	 * Atomically sets to the specified value and returns the old value.
	 *
	 * @param value the new value
	 * @return the previous value
	 */
	@ThreadSafe
	T getAndSet(final T value);

	/**
	 * Marks itself as updated.
	 */
	@ThreadSafe
	void toDirty();

	@ThreadSafe
	boolean equals( final Object value );

	/**
	 * If the value is {@link java.util.Collection}, {@link java.util.Map}, {@link com.fasterxml.jackson.databind.node.ArrayNode} or {@link com.fasterxml.jackson.databind.node.ObjectNode}, returns the its size.
	 * If the value is an array, returns the its length.
	 * If the value is null, returns 0.
	 * For anything else, returns 1.
	 *
	 * @return the size of this value
	 */
	@ThreadSafe
	int size();

	/**
	 * Returns true if the size is zero.
	 *
	 * @return {boolean} true if the size is zero
	 */
	@ThreadSafe
	boolean isEmpty();

	/**
	 * Returns true if the value is null.
	 *
	 * @return true if the value is null
	 */
	@ThreadSafe
	boolean isNull();

	/**
	 * Returns true if the value is not null.
	 *
	 * @return true if the value is not null
	 */
	@ThreadSafe
	boolean isNotNull();

	/**
	 * If the value is {@link java.util.List}, {@link com.fasterxml.jackson.databind.node.ArrayNode} or an array, returns the index of the specified value.
	 * Otherwise, returns -1.
	 *
	 * @param value value to search for
	 * @return the index of the specified value
	 */
	int indexOf( final Object value );

	/**
	 * If the value is {@link java.util.List}, {@link com.fasterxml.jackson.databind.node.ArrayNode} or an array, returns the last index of the specified value.
	 * Otherwise, returns -1.
	 *
	 * @param value value to search for
	 * @return the last index of the specified value
	 */
	int lastIndexOf( final Object value );

	/**
	 * Returns true if this contains the specified value.
	 *
	 * @param value value to search for
	 * @return true if this contains the specified value
	 */
	boolean contains( final Object value );

	/**
	 * Returns true if this contains all the specified values.
	 *
	 * @param values values to search for
	 * @return true if this contains all the specified values
	 */
	boolean containsAll( final Collection<?> values );
}
