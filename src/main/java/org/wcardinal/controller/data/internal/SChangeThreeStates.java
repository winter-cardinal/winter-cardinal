/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

public class SChangeThreeStates implements SChange {
	public Object[] parameters;

	public SChangeThreeStates(final Object added, final Object removed, final Object updated ) {
		parameters = new Object[]{ added, removed, updated };
	}

	@Override
	public Object[] toParameters() {
		return parameters;
	}
}
