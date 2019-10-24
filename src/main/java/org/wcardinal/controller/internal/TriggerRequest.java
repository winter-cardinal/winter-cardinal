/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.List;

import org.jdeferred.Deferred;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.controller.TriggerErrors;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
public class TriggerRequest {
	public final List<String[]> types;
	public final List<Object> arguments;

	@JsonIgnore
	public final Deferred<List<JsonNode>, TriggerErrors, Integer> deferred;

	@JsonCreator
	public TriggerRequest(
		@JsonProperty( "types" ) final List<String[]> types,
		@JsonProperty( "arguments" ) final List<Object> arguments
	){
		this( types, arguments, null );
	}

	public TriggerRequest( final List<String[]> types, final List<Object> arguments, final Deferred<List<JsonNode>, TriggerErrors, Integer> deferred ){
		this.types = types;
		this.arguments = arguments;
		this.deferred = deferred;
	}
}
