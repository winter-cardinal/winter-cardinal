/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DatabindException;

class Deserializers {
	static void reportWrongTokenException( JsonParser parser, JsonToken expToken ) throws JacksonException {
		final String msg = String.format( "Unexpected token (%s), expected %s",
			parser.currentToken(), expToken);
		throw DatabindException.from( parser, msg );
	}

	static void checkEndArray( final JsonParser parser ) throws JacksonException {
		if( parser.nextToken() != JsonToken.END_ARRAY ) {
			reportWrongTokenException(parser, JsonToken.END_ARRAY);
		}
	}

}
