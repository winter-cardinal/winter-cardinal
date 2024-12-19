/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class StreamedResultSerializer extends JsonSerializer<StreamedResult> {
	@Override
	public void serialize( final StreamedResult streamedResult, final JsonGenerator gen, final SerializerProvider serializers ) throws IOException, JsonProcessingException {
		streamedResult.serialize(gen, serializers);
	}
}
