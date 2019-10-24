/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.wcardinal.controller.data.internal.SChange;
import org.wcardinal.controller.data.internal.SParent;

public interface ControllerOnChangeHandler {
	void handle( SParent origin, SParent parent, String name, SChange schange );
}
