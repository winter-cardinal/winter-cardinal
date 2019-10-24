/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;

import org.wcardinal.controller.data.SJsonNode;
import org.wcardinal.util.json.Json;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SJsonNodeImpl extends SScalarImpl<JsonNode> implements SJsonNode {
	public SJsonNodeImpl() {
		super( SType.JSON_NODE.ordinal() );
	}

	@Override
	JsonNode cast(final JsonNode valueElement) throws Exception {
		return valueElement;
	}

	@Override
	JsonNode makeNonNullValue() {
		return null;
	}

	@Override
	public ArrayNode createArrayNode() {
		final ArrayNode node = Json.mapper.createArrayNode();
		set( node );
		return node;
	}

	@Override
	public ObjectNode createObjectNode() {
		final ObjectNode node = Json.mapper.createObjectNode();
		set( node );
		return node;
	}

	@Override
	public boolean equals( final JsonNode target ) {
		return Objects.equal( value, target );
	}
}
