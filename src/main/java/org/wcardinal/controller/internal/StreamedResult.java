/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize( using=StreamedResultSerializer.class )
public class StreamedResult {
	private final String value;

	public StreamedResult( final String value ) {
		this.value = value;
	}

	public void serialize( final JsonGenerator gen, final SerializationContext ctxt ) throws JacksonException {
		gen.writeRawValue( this.value );
	}
}
