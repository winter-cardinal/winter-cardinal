/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;

public class SMapPatchesPackedImplDeserializer extends ValueDeserializer<SPatchesPacked<?, ?>> {
	final JavaType valueType;

	public SMapPatchesPackedImplDeserializer(){
		this.valueType = null;
	}

	public SMapPatchesPackedImplDeserializer( final JavaType valueType ) {
		this.valueType = valueType;
	}

	@Override
	public ValueDeserializer<?> createContextual( final DeserializationContext ctxt, final BeanProperty property) {
		final JavaType javaType = (property == null ? ctxt.getContextualType() : property.getType());
		final JavaType valueType = javaType.containedType( 0 );
		final SMapPatchesPackedImplDeserializer deserializer = new SMapPatchesPackedImplDeserializer( valueType );
		return deserializer;
	}

	@Override
	public SPatchesPacked<?, ?> deserialize( final JsonParser parser, final DeserializationContext ctxt ) throws JacksonException {
		return SMapPatchesPackedImpl.deserialize( parser, ctxt, valueType );
	}
}
