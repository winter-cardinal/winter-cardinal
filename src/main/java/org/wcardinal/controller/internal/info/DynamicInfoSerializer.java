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

public class DynamicInfoSerializer extends JsonSerializer<DynamicInfo> {
	@Override
	public void serialize( final DynamicInfo info, final JsonGenerator gen, final SerializerProvider serializers ) throws IOException, JsonProcessingException {
		gen.writeStartArray();

		// nameToData
		if( info.nameToData != null ) {
			gen.writeStartArray();
			gen.writeStartArray();
			for( final String key: info.nameToData.keySet() ) {
				gen.writeString( key );
			}
			gen.writeEndArray();
			gen.writeStartArray();
			for( final DynamicDataObject value: info.nameToData.values() ) {
				gen.writeObject( value );
			}
			gen.writeEndArray();
			gen.writeEndArray();
		} else {
			gen.writeNull();
		}

		// nameToInfo
		if( info.nameToInfo != null ) {
			gen.writeStartArray();
			gen.writeStartArray();
			for( final String key: info.nameToInfo.keySet() ) {
				gen.writeString( key );
			}
			gen.writeEndArray();
			gen.writeStartArray();
			for( final DynamicInfo value: info.nameToInfo.values() ) {
				gen.writeObject( value );
			}
			gen.writeEndArray();
			gen.writeEndArray();
		} else {
			gen.writeNull();
		}

		gen.writeEndArray();
	}
}
