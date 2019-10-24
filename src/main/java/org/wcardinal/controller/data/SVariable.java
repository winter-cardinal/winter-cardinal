/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * Represents the synchronizable variable.
 */
public interface SVariable {
	/**
	 * Initialize this.
	 */
	@ThreadSafe
	void initialize();

	/**
	 * Returns true if this is read-only.
	 *
	 * @return true if this is read-only
	 */
	@ThreadSafe
	boolean isReadOnly();

	/**
	 * Returns true if this is non-null.
	 *
	 * @return true if this is non-null
	 */
	@ThreadSafe
	boolean isNonNull();

	/**
	 * Returns true if this is initialized.
	 *
	 * @return true if this is initialized
	 */
	@ThreadSafe
	boolean isInitialized();
}
