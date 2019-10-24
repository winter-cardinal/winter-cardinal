/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * Represents the integer data.
 */
public interface SInteger extends SNumber<Integer> {
	/**
	 * Returns true if the specified target is equal to this.
	 *
	 * @param target the target to be compared
	 * @return true if the specified target is equal to this
	 */
	@ThreadSafe
	boolean equals( Integer target );

	/**
	 * Returns true if the specified target is equal to this.
	 *
	 * @param target the target to be compared
	 * @return true if the specified target is equal to this
	 */
	@ThreadSafe
	boolean equals( int target );
}
