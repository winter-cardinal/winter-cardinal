/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;

public class SPatchUtil {
	private SPatchUtil() {}

	static void throwJsonParseException( final JsonParser parser ) throws JsonParseException {
		throw new JsonParseException( parser, "Unexpected token" );
	}

	static void assertCurrentToken( final JsonParser parser, final JsonToken token ) throws JsonParseException {
		if( parser.getCurrentToken() != token ) {
			throwJsonParseException( parser );
		}
	}

	static void assertNextToken( final JsonParser parser, final JsonToken token ) throws IOException {
		if( parser.nextToken() != token ) {
			throwJsonParseException( parser );
		}
	}

	static <V> V readValue( final JsonToken token, final JsonParser parser, final DeserializationContext ctxt, JavaType valueType ) throws IOException {
		if( token == JsonToken.VALUE_NULL ) {
			return null;
		} else {
			return ctxt.readValue( parser, valueType );
		}
	}

	static <V> V readCurrentValue( final JsonParser parser, final DeserializationContext ctxt, JavaType valueType ) throws IOException {
		return readValue( parser.getCurrentToken(), parser, ctxt, valueType );
	}

	static <V> V readNextValue( final JsonParser parser, final DeserializationContext ctxt, JavaType valueType ) throws IOException {
		return readValue( parser.nextToken(), parser, ctxt, valueType );
	}
}
