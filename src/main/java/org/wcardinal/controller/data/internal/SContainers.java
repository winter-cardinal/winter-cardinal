/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

public class SContainers {
	private final static String ID_PREFIX = "$$";

	public static String toId( final String name, final String type ) {
		return ID_PREFIX+name+"@"+type;
	}

	public static boolean isId( final String name ) {
		return name.startsWith( ID_PREFIX );
	}

	public static boolean isNotId( final String name ) {
		return ! isId( name );
	}
}
