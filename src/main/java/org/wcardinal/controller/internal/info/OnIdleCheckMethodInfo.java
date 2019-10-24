/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

public class OnIdleCheckMethodInfo {
	public final boolean hasLockRequired;
	public final boolean hasLockNotRequired;

	public OnIdleCheckMethodInfo( final boolean hasLockRequired, final boolean hasLockNotRequired ){
		this.hasLockRequired = hasLockRequired;
		this.hasLockNotRequired = hasLockNotRequired;
	}
}
