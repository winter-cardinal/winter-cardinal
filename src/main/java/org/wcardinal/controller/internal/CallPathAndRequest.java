/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CallPathAndRequest {
	public String[] path;
	public CallRequest request;

	@JsonCreator
	public CallPathAndRequest(
		@JsonProperty( value="path", required=true ) final String[] path,
		@JsonProperty( value="request", required=true ) final CallRequest request
	) {
		this.path = path;
		this.request = request;
	}
}
