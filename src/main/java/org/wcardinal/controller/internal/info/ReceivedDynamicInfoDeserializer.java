/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.HashMap;
import java.util.Map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class ReceivedDynamicInfoDeserializer extends ValueDeserializer<ReceivedDynamicInfo> {
	@Override
	public ReceivedDynamicInfo deserialize( final JsonParser parser, final DeserializationContext ctxt ) throws JacksonException {
		if( parser.currentToken() == JsonToken.VALUE_NULL ) {
			return null;
		}

		if( parser.currentToken() != JsonToken.START_ARRAY ) {
			Deserializers.reportWrongTokenException(parser, JsonToken.START_ARRAY);
		}

		// nameToData
		Map<String, DynamicDataJsonNode> nameToData = null;
		if( parser.nextToken() == JsonToken.START_ARRAY ) {
			parser.nextToken();
			final String[] keys = ctxt.readValue(parser, String[].class);
			parser.nextToken();
			final DynamicDataJsonNode[] values = ctxt.readValue(parser, DynamicDataJsonNode[].class);
			if( keys != null && values != null && keys.length == values.length ) {
				nameToData = new HashMap<>();
				for( int i=0; i<keys.length; ++i ) {
					final String key = keys[ i ];
					nameToData.put( key, values[ i ] );
				}
			}
			Deserializers.checkEndArray( parser );
		}

		// nameToInfo
		Map<String, ReceivedDynamicInfo> nameToInfo = null;
		if( parser.nextToken() == JsonToken.START_ARRAY ) {
			parser.nextToken();
			final String[] keys = ctxt.readValue(parser, String[].class);
			parser.nextToken();
			final ReceivedDynamicInfo[] values = ctxt.readValue(parser, ReceivedDynamicInfo[].class);
			if( keys != null && values != null && keys.length == values.length ) {
				nameToInfo = new HashMap<>();
				for( int i=0; i<keys.length; ++i ) {
					nameToInfo.put( keys[ i ], values[ i ] );
				}
			}
			Deserializers.checkEndArray( parser );
		}

		Deserializers.checkEndArray( parser );

		return new ReceivedDynamicInfo( nameToData, nameToInfo );
	}
}
