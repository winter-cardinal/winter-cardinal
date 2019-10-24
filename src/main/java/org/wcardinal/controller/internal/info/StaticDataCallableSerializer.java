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

public class StaticDataCallableSerializer extends JsonSerializer<StaticDataCallable> {
	@Override
	public void serialize( final StaticDataCallable data, final JsonGenerator gen, final SerializerProvider serializers ) throws IOException, JsonProcessingException {
		gen.writeStartArray();
		gen.writeNumber( StaticDataType.CALLABLE.ordinal() );
		gen.writeNumber( data.timeout );
		gen.writeNumber( Properties.toInt( data.properties ) );
		gen.writeEndArray();
	}
}
