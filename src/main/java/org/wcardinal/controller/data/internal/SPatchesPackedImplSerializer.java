/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class SPatchesPackedImplSerializer extends ValueSerializer<SPatchesPacked<?, ?>> {
	public SPatchesPackedImplSerializer(){}

	@Override
    public void serialize(final SPatchesPacked<?, ?> value, final JsonGenerator gen, final SerializationContext ctxt) throws JacksonException {
		value.serialize( gen );
	}
}
