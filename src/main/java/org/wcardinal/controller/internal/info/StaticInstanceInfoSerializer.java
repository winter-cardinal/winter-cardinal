/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class StaticInstanceInfoSerializer extends ValueSerializer<StaticInstanceInfo> {
	@Override
	public void serialize( final StaticInstanceInfo info, final JsonGenerator gen, final SerializationContext serializers ) throws JacksonException {
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
			for( final Object value: info.nameToData.values() ) {
				gen.writePOJO( value );
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
			for( final StaticInstanceInfo value: info.nameToInfo.values() ) {
				gen.writePOJO( value );
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
				gen.writePOJO( value );
			}
			gen.writeEndArray();
			gen.writeEndArray();
		} else {
			gen.writeNull();
		}

		gen.writeEndArray();
	}
}
