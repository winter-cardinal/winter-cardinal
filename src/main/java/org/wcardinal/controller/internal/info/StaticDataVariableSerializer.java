/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import org.wcardinal.controller.internal.Properties;

public class StaticDataVariableSerializer extends ValueSerializer<StaticDataVariable> {
	@Override
	public void serialize( final StaticDataVariable data, final JsonGenerator gen, final SerializationContext ctxt ) throws JacksonException {
		gen.writeStartArray();
		gen.writeNumber( StaticDataType.VARIABLE.ordinal() );
		gen.writeNumber( data.type );
		gen.writeNumber( Properties.toInt( data.properties ) );
		gen.writeEndArray();
	}
}
