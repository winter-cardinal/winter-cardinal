/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;

import org.wcardinal.controller.data.SObjectNode;
import org.wcardinal.util.json.Json;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SObjectNodeImpl extends SScalarImpl<ObjectNode> implements SObjectNode {
	public SObjectNodeImpl() {
		super( SType.OBJECT_NODE.ordinal() );
	}

	@Override
	ObjectNode cast(final JsonNode valueElement) throws Exception {
		if( valueElement == null || valueElement.isNull() || valueElement.isObject() != true ) return null;
		return (ObjectNode) valueElement;
	}

	@Override
	ObjectNode makeNonNullValue() {
		return Json.mapper.createObjectNode();
	}

	@Override
	public ObjectNode create() {
		final ObjectNode node = Json.mapper.createObjectNode();
		set( node );
		return node;
	}

	@Override
	public boolean equals( final ObjectNode target ) {
		return Objects.equal( value, target );
	}
}
