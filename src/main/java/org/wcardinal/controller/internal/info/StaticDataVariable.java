/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.EnumSet;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.wcardinal.controller.internal.Property;

@JsonSerialize( using=StaticDataVariableSerializer.class )
public class StaticDataVariable implements StaticData {
	public final int type;
	public final EnumSet<Property> properties;

	public StaticDataVariable( final int type, final EnumSet<Property> properties ){
		this.type = type;
		this.properties = properties;
	}
}
