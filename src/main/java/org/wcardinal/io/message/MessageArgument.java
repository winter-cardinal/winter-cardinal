/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

import org.wcardinal.controller.internal.RootControllerLockResult;

public interface MessageArgument {
	RootControllerLockResult lock();
}
