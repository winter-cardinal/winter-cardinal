/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class AckInfoSerializer extends ValueSerializer<AckInfo> {
	@Override
	public void serialize( final AckInfo info, final JsonGenerator gen, final SerializationContext ctxt ) throws JacksonException {
		gen.writePOJO( info.senderIds );
	}
}
