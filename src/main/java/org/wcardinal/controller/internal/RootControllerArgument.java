/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.wcardinal.io.message.MessageArgument;

public class RootControllerArgument implements MessageArgument {
	final RootController rootController;

	public RootControllerArgument( final RootController rootController ){
		this.rootController = rootController;
	}

	@Override
	public RootControllerLockResult lock() {
		return rootController.lockDynamicInfo();
	}
}
