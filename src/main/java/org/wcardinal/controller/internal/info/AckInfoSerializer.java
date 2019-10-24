/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class AckInfoSerializer extends JsonSerializer<AckInfo> {
	@Override
	public void serialize( final AckInfo info, final JsonGenerator gen, final SerializerProvider serializers ) throws IOException, JsonProcessingException {
		gen.writeObject( info.senderIds );
	}
}
