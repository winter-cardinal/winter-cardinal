/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class AckInfoDeserializer extends JsonDeserializer<AckInfo> {
	@Override
	public AckInfo deserialize( final JsonParser parser, final DeserializationContext ctxt ) throws IOException, JsonProcessingException {
		if( parser.getCurrentToken() == JsonToken.VALUE_NULL ) {
			return null;
		} else if( parser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT ){
			return new AckInfo( parser.getLongValue() );
		} else {
			return new AckInfo( ctxt.readValue(parser, long[].class) );
		}
	}
}
