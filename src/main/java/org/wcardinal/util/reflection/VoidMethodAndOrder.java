/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;

public class VoidMethodAndOrder extends MethodAndOrder<Void> {
	public VoidMethodAndOrder(final Method method, final int[] order) {
		super(method, order);
	}
}
