/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.EnumSet;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.wcardinal.controller.internal.Property;

@JsonSerialize( using=StaticDataCallableSerializer.class )
public class StaticDataCallable implements StaticData {
	public final long timeout;
	public final EnumSet<Property> properties;

	public StaticDataCallable( final long timeout, final EnumSet<Property> properties ){
		this.timeout = timeout;
		this.properties = properties;
	}
}
