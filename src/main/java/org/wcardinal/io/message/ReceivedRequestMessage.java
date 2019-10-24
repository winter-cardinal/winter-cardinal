/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

import com.fasterxml.jackson.databind.JsonNode;

public class ReceivedRequestMessage {
	final String type;
	final JsonNode arguments;

	public ReceivedRequestMessage( final String type, final JsonNode arguments ) throws Exception {
		this.type = type;
		this.arguments = arguments;
	}

	public String getType() {
		return type;
	}

	public JsonNode getArguments() {
		return arguments;
	}

	@Override
	public String toString(){
		return "{type:"+type+", args:"+arguments+"}";
	}
}
