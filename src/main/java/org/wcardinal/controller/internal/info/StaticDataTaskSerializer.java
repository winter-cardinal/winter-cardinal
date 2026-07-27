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

public class StaticDataTaskSerializer extends ValueSerializer<StaticDataTask> {
	@Override
	public void serialize( final StaticDataTask data, final JsonGenerator gen, final SerializationContext serializers ) throws JacksonException {
		gen.writeStartArray();
		gen.writeNumber( StaticDataType.TASK.ordinal() );
		gen.writeNumber( Properties.toInt( data.properties ) );
		gen.writeEndArray();
	}
}
