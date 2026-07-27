/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

public class Json {
	private Json(){}
	public final static ObjectMapper mapper = new ObjectMapper();
	public final static ObjectWriter non_closing_writer = mapper.writer().without(StreamWriteFeature.AUTO_CLOSE_TARGET);

	public static <T> T convert( final JsonNode fromValue, final Class<T> type ) {
		return mapper.<T>convertValue( fromValue, type );
	}

	public static <T> T convert( final JsonNode fromValue, final JavaType type ) {
		return mapper.<T>convertValue( fromValue, type );
	}

	public static <T> T convert( final String json, final Class<T> type ) throws JacksonException {
		return mapper.<T>readValue( json, type );
	}

	public static <T> T convert( final String json, final JavaType type ) throws JacksonException {
		return mapper.<T>readValue( json, type );
	}

	public static String convert( final Object object ) throws JacksonException {
		return mapper.writeValueAsString( object );
	}

	public static JavaType typeOf( final Class<?> type ) {
		return mapper.getTypeFactory().constructType( type );
	}

	public static JavaType typeOf( final Class<?> baseType, final Class<?>... types ) {
		return mapper.getTypeFactory().constructParametricType( baseType, types );
	}

	public static JavaType typeOf( final Class<?> baseType, final JavaType... types ) {
		return mapper.getTypeFactory().constructParametricType( baseType, types );
	}
}
