/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

public class SChangeTwoStates implements SChange {
	public Object[] parameters;

	public SChangeTwoStates(final Object newValue, final Object oldValue) {
		parameters = new Object[]{ newValue, oldValue };
	}

	@Override
	public Object[] toParameters() {
		return parameters;
	}
}
