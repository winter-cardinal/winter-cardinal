/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.wcardinal.controller.internal.ControllerType;
import org.wcardinal.controller.internal.Properties;
import org.wcardinal.controller.internal.Property;

@JsonSerialize( using=StaticInfoSerializer.class )
public class StaticInfo extends Info<StaticData, StaticInfo> {
	public final Map<String, Object> constants;
	public final int type;
	public final int properties;

	public StaticInfo( final ControllerType type, final EnumSet<Property> properties ) {
		super();
		this.constants = new HashMap<>();
		this.type = type.ordinal();
		this.properties = Properties.toInt( properties );
	}
}
