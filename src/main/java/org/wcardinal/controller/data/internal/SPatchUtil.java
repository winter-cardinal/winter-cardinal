/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;

public class SPatchUtil {
	private SPatchUtil() {}

	static void throwJsonParseException( final JsonParser parser ) throws JacksonException {
		throw new StreamReadException( parser, "Unexpected token" );
	}

	static void assertCurrentToken( final JsonParser parser, final JsonToken token ) throws JacksonException {
		if( parser.currentToken() != token ) {
			throwJsonParseException( parser );
		}
	}

	static void assertNextToken( final JsonParser parser, final JsonToken token ) throws JacksonException {
		if( parser.nextToken() != token ) {
			throwJsonParseException( parser );
		}
	}

	static <V> V readValue( final JsonToken token, final JsonParser parser, final DeserializationContext ctxt, JavaType valueType ) throws JacksonException {
		if( token == JsonToken.VALUE_NULL ) {
			return null;
		} else {
			return ctxt.readValue( parser, valueType );
		}
	}

	static <V> V readCurrentValue( final JsonParser parser, final DeserializationContext ctxt, JavaType valueType ) throws JacksonException {
		return readValue( parser.currentToken(), parser, ctxt, valueType );
	}

	static <V> V readNextValue( final JsonParser parser, final DeserializationContext ctxt, JavaType valueType ) throws JacksonException {
		return readValue( parser.nextToken(), parser, ctxt, valueType );
	}
}
