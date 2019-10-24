/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.wcardinal.controller.internal.Properties;

public class StaticDataTaskSerializer extends JsonSerializer<StaticDataTask> {
	@Override
	public void serialize( final StaticDataTask data, final JsonGenerator gen, final SerializerProvider serializers ) throws IOException, JsonProcessingException {
		gen.writeStartArray();
		gen.writeNumber( StaticDataType.TASK.ordinal() );
		gen.writeNumber( Properties.toInt( data.properties ) );
		gen.writeEndArray();
	}
}
