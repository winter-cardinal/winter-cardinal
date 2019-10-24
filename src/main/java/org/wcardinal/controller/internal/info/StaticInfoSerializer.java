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

public class StaticInfoSerializer extends JsonSerializer<StaticInfo> {
	@Override
	public void serialize( final StaticInfo info, final JsonGenerator gen, final SerializerProvider serializers ) throws IOException, JsonProcessingException {
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
			for( final StaticData value: info.nameToData.values() ) {
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
			for( final StaticInfo value: info.nameToInfo.values() ) {
				gen.writeObject( value );
			}
			gen.writeEndArray();
			gen.writeEndArray();
		} else {
			gen.writeNull();
		}

		// Constants
		if( info.constants != null ) {
			gen.writeStartArray();
			gen.writeStartArray();
			for( final String key: info.constants.keySet() ) {
				gen.writeString( key );
			}
			gen.writeEndArray();
			gen.writeStartArray();
			for( final Object value: info.constants.values() ) {
				gen.writeObject( value );
			}
			gen.writeEndArray();
			gen.writeEndArray();
		} else {
			gen.writeNull();
		}

		// Type
		gen.writeNumber( info.type );

		// Properties
		gen.writeNumber( info.properties );

		gen.writeEndArray();
	}
}
