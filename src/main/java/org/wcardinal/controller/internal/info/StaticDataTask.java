/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.EnumSet;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.wcardinal.controller.internal.Property;

@JsonSerialize( using=StaticDataTaskSerializer.class )
public class StaticDataTask implements StaticData {
	public final EnumSet<Property> properties;

	public StaticDataTask( final EnumSet<Property> properties ){
		this.properties = properties;
	}
}
