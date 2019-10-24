/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.lang.reflect.Field;
import java.util.EnumSet;

import org.springframework.core.ResolvableType;

import org.wcardinal.controller.data.internal.SType;
import org.wcardinal.controller.internal.info.StaticDataVariable;

public class ControllerFactoryFieldInfo {
	final String name;
	final Field field;
	final SType type;
	final MethodTypeChecker checker;
	final StaticDataVariable data;
	final EnumSet<Property> properties;
	final ResolvableType[] generics;

	ControllerFactoryFieldInfo( final String name, final Field field, final SType type, final MethodTypeChecker checker, final StaticDataVariable data, final EnumSet<Property> properties, final ResolvableType[] generics ){
		this.name = name;
		this.field = field;
		this.type = type;
		this.checker = checker;
		this.data = data;
		this.properties = properties;
		this.generics = generics;
	}
}
