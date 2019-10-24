/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * Represents the JSON array data.
 */
public interface SArrayNode extends SScalar<ArrayNode> {
	/**
	 * Creates a new JSON array, sets to it and returns it.
	 *
	 * @return the new JSON array
	 */
	@ThreadSafe
	ArrayNode create();

	/**
	 * Returns true if the specified target is equal to this.
	 *
	 * @param target the target to be compared
	 * @return true if the specified target is equal to this
	 */
	@ThreadSafe
	boolean equals( ArrayNode target );
}
