/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ReceivedDynamicInfoDeserializer extends JsonDeserializer<ReceivedDynamicInfo> {
	@Override
	public ReceivedDynamicInfo deserialize( final JsonParser parser, final DeserializationContext ctxt ) throws IOException, JsonProcessingException {
		if( parser.getCurrentToken() == JsonToken.VALUE_NULL ) {
			return null;
		}

		if( parser.getCurrentToken() != JsonToken.START_ARRAY ) {
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
