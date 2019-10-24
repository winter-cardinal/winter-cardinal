/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
public class DynamicDataJsonNode extends DynamicData<JsonNode> {
	@JsonCreator
	public DynamicDataJsonNode(
		@JsonProperty("revision") final long revision,
		@JsonProperty("type") final int type,
		@JsonProperty("data") final JsonNode data
	) {
		super(revision, type, data);
	}
}
