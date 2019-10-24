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
import com.google.common.base.Objects;

import org.wcardinal.controller.data.SArrayNode;
import org.wcardinal.util.json.Json;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SArrayNodeImpl extends SScalarImpl<ArrayNode> implements SArrayNode {
	public SArrayNodeImpl() {
		super( SType.ARRAY_NODE.ordinal() );
	}

	@Override
	ArrayNode cast(final JsonNode valueElement) throws Exception {
		if( valueElement == null || valueElement.isNull() || valueElement.isArray() != true ) return null;
		return (ArrayNode) valueElement;
	}

	@Override
	ArrayNode makeNonNullValue() {
		return Json.mapper.createArrayNode();
	}

	@Override
	public ArrayNode create() {
		final ArrayNode node = Json.mapper.createArrayNode();
		set( node );
		return node;
	}

	@Override
	public boolean equals( final ArrayNode target ) {
		return Objects.equal( value, target );
	}
}
