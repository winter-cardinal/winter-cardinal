/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;

class Deserializers {
	static void reportWrongTokenException( JsonParser parser, JsonToken expToken ) throws JsonMappingException {
		final String msg = String.format( "Unexpected token (%s), expected %s",
			parser.getCurrentToken(), expToken);
		throw JsonMappingException.from( parser, msg );
	}

	static void checkEndArray( final JsonParser parser ) throws IOException {
		if( parser.nextToken() != JsonToken.END_ARRAY ) {
			reportWrongTokenException(parser, JsonToken.END_ARRAY);
		}
	}

}
