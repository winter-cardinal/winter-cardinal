/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Field;
import java.util.EnumSet;

public class ControllerFactoryControllerInfo {
	final String name;
	final Field field;
	final ControllerType type;
	final EnumSet<Property> properties;
	final ControllerFactory factory;
	final ControllerFactory childFactory;

	ControllerFactoryControllerInfo( final String name, final Field field, final ControllerType type, final EnumSet<Property> properties, final ControllerFactory factory, final ControllerFactory childFactory ){
		this.name = name;
		this.field = field;
		this.type = type;
		this.properties = properties;
		this.factory = factory;
		this.childFactory = childFactory;
	}
}
