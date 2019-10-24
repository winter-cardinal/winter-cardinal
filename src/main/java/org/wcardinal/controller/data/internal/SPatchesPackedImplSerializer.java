/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class SPatchesPackedImplSerializer extends JsonSerializer<SPatchesPacked<?, ?>> {
	public SPatchesPackedImplSerializer(){}

	@Override
	public void serialize( final SPatchesPacked<?, ?> value, final JsonGenerator gen, final SerializerProvider serializers ) throws IOException, JsonProcessingException {
		value.serialize( gen );
	}
}
