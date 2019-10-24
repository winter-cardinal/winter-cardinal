/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

/**
 * The function object for retrieving keys from values.
 *
 * @param <V> the value class
 */
public interface SKeyOf<V> {
	/**
	 * Returns the key of the specified value.
	 *
	 * @param value the value
	 * @return the key of the specified value
	 */
	String keyOf( final V value );
}
