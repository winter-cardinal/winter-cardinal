/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jdeferred.Deferred;

import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.controller.TriggerErrors;

public class TriggerRequests {
	final static Pattern SPLIT_PATTERN = Pattern.compile( "\\s+" );
	final static Pattern NAMESPACE_PATTERN = Pattern.compile( "\\." );
	final static String ASTERISK = "*";

	static TriggerRequest create( final String name, final Object[] args, final Deferred<List<JsonNode>, TriggerErrors, Integer> deferred ) {
		final List<String[]> types = new ArrayList<>();
		final String[] splittedNames = SPLIT_PATTERN.split( name.trim() );
		for( int i=0, imax=splittedNames.length; i<imax; ++i ){
			final String splittedName = splittedNames[ i ];
			final String[] type = NAMESPACE_PATTERN.split( splittedName );
			if( splittedName.startsWith(".") ) type[ i ] = ASTERISK;
			types.add( type );
		}

		final List<Object> arguments = new ArrayList<>();
		arguments.add( null );
		for( final Object arg: args ) {
			arguments.add( arg );
		}

		return new TriggerRequest( types, arguments, deferred );
	}
}
