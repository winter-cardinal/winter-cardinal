/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.scope;

import org.springframework.core.NamedThreadLocal;

public class ControllerScopeAttributesHolder {
	private static final ThreadLocal<ControllerScopeAttributes> attributeHolder =
			new NamedThreadLocal<ControllerScopeAttributes>("Controller attribute");

	private ControllerScopeAttributesHolder(){}

	public static ControllerScopeAttributes get() throws IllegalStateException {
		final ControllerScopeAttributes result = attributeHolder.get();
		if (result == null) {
			throw new IllegalStateException(
				"No thread-bound Controller attributes found. " +
				"Your code is probably not running in controller request thread?");
		}
		return result;
	}

	public static void remove() {
		attributeHolder.remove();
	}

	public static void set(final ControllerScopeAttributes attribute) {
		if (attribute != null) {
			attributeHolder.set(attribute);
		} else {
			remove();
		}
	}
}
