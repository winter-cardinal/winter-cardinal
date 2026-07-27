/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class StreamedResultSerializer extends ValueSerializer<StreamedResult> {
	@Override
	public void serialize( final StreamedResult streamedResult, final JsonGenerator gen, final SerializationContext ctxt ) throws JacksonException {
		streamedResult.serialize( gen, ctxt );
	}
}
