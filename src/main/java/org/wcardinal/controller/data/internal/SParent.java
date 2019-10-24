/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

public interface SParent {
	void onChange( SParent origin, SParent parent, String name, SChange change );
	void update();
	void onUnlock();
}
