/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Comparator;

class SNavigableMapDescendingComparator implements Comparator<String> {
	@Override
	public int compare( final String o1, final String o2 ) {
		return o2.compareTo(o1);
	}
}
