/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * Represents the JSON object data.
 */
public interface SObjectNode extends SScalar<ObjectNode> {
	/**
	 * Creates a new JSON object, sets to it and returns it.
	 *
	 * @return the new JSON object
	 */
	@ThreadSafe
	ObjectNode create();

	/**
	 * Returns true if the specified target is equal to this.
	 *
	 * @param target the target to be compared
	 * @return true if the specified target is equal to this
	 */
	@ThreadSafe
	boolean equals( ObjectNode target );
}
