/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util;

public class Pair<T, U> {
	public final T left;
	public final U right;

	public Pair(){
		this.left = null;
		this.right = null;
	}

	public <ET extends T, EU extends U>Pair(final ET left, final EU right) {
		this.left = left;
		this.right = right;
	}
}
