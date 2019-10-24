/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.util.json.Json;

public class ReceivedRequestMessages {
	private ReceivedRequestMessages(){}

	public static List<ReceivedRequestMessage> parse( final String messageString ) throws MessageFormatException {
		final List<ReceivedRequestMessage> result = new ArrayList<>();
		try{
			final JsonNode nodes = Json.mapper.readTree( messageString );
			for( final JsonNode node: nodes ){
				result.add( parse( node ) );
			}
		} catch( Exception e ){
			 throw new MessageFormatException();
		}
		return result;
	}

	public static List<ReceivedRequestMessage> parse( final Reader reader ) throws MessageFormatException {
		final List<ReceivedRequestMessage> result = new ArrayList<>();
		try{
			final JsonNode nodes = Json.mapper.readTree( reader );
			for( final JsonNode node: nodes ){
				result.add( parse( node ) );
			}
		} catch( Exception e ){
			throw new MessageFormatException();
		}
		return result;
	}

	public static ReceivedRequestMessage parse( final JsonNode node ) throws Exception {
		if( node.isNull() || node.isArray() != true || node.size() != 2 ) throw new MessageFormatException();
		final String type = node.get( 0 ).asText();
		final JsonNode arguments = node.get( 1 );
		return new ReceivedRequestMessage( type, arguments );
	}
}
