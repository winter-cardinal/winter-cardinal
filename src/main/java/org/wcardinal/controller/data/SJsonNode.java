/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * Represents the JSON data.
 */
public interface SJsonNode extends SScalar<JsonNode> {
	/**
	 * Creates a JSON object, sets to it and returns it.
	 *
	 * @return the new JSON object
	 */
	@ThreadSafe
	ObjectNode createObjectNode();

	/**
	 * Creates a JSON array, sets to it and returns it.
	 *
	 * @return the new JSON array
	 */
	@ThreadSafe
	ArrayNode createArrayNode();

	/**
	 * Returns true if the specified target is equal to this.
	 *
	 * @param target the target to be compared
	 * @return true if the specified target is equal to this
	 */
	@ThreadSafe
	boolean equals( JsonNode target );
}
