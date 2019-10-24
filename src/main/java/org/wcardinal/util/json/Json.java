/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Json {
	private Json(){}
	public final static ObjectMapper mapper = new ObjectMapper();
	public final static ObjectWriter non_closing_writer = mapper.writer().without(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

	public static <T> T convert( final JsonNode fromValue, final Class<T> type ) {
		return mapper.<T>convertValue( fromValue, type );
	}

	public static <T> T convert( final JsonNode fromValue, final JavaType type ) {
		return mapper.<T>convertValue( fromValue, type );
	}

	public static <T> T convert( final String json, final Class<T> type ) throws JsonParseException, JsonMappingException, IOException {
		return mapper.<T>readValue( json, type );
	}

	public static <T> T convert( final String json, final JavaType type ) throws JsonParseException, JsonMappingException, IOException {
		return mapper.<T>readValue( json, type );
	}

	public static String convert( final Object object ) throws JsonProcessingException {
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
