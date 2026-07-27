/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class AckInfoDeserializer extends ValueDeserializer<AckInfo> {
	@Override
	public AckInfo deserialize( final JsonParser parser, final DeserializationContext ctxt ) throws JacksonException {
		if( parser.currentToken() == JsonToken.VALUE_NULL ) {
			return null;
		} else if( parser.currentToken() == JsonToken.VALUE_NUMBER_INT ){
			return new AckInfo( parser.getLongValue() );
		} else {
			return new AckInfo( ctxt.readValue(parser, long[].class) );
		}
	}
}
