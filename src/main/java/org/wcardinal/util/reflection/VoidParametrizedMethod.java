/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

public class VoidParametrizedMethod extends ParametrizedMethod<Void> {
	public VoidParametrizedMethod( final Method method ) {
		super( method );
	}
}
