/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import org.wcardinal.util.doc.ThreadSafe;

public interface SNumber<T extends Number> extends SScalar<T>, Comparable<T> {
	/**
	 * Atomically adds the specified value to the current value if the current value is not null.
	 *
	 * @param delta value to add
	 * @return updated value
	 * @throws NullPointerException if the specified value is null
	 */
	T addAndGet(final T delta);

	/**
	 * Atomically decrements by one the current value if the current value is not null.
	 *
	 * @return updated value
	 */
	T decrementAndGet();

	/**
	 * Atomically add the specified value to the current value if the current value is not null.
	 *
	 * @param delta value to add
	 * @return previous value
	 * @throws NullPointerException if the specified value is null
	 */
	T getAndAdd(final T delta);

	/**
	 * Atomically decrements by one the current value if the current value is not null.
	 *
	 * @return previous value
	 */
	T getAndDecrement();

	/**
	 * Atomically increments by one the current value if the current value is not null.
	 *
	 * @return previous value
	 */
	T getAndIncrement();

	/**
	 * Atomically increments by one the current value if the current value is not null.
	 *
	 * @return updated value
	 */
	T incrementAndGet();

	/**
	 * Returns true if the specified target is equal to this.
	 *
	 * @param target the target to be compared
	 * @return true if the specified target is equal to this
	 */
	@ThreadSafe
	boolean equals( Number target );
}
