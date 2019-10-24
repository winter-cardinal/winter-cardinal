/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
public class CallRequest {
	public final String name;
	public final ArrayNode parameters;

	@JsonCreator
	public CallRequest(
		@JsonProperty( "name" ) final String name,
		@JsonProperty( "parameters" ) final ArrayNode parameters
	) {
		this.name = name;
		this.parameters = parameters;
	}
}
