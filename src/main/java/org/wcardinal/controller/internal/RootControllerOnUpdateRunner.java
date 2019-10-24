/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

public class RootControllerOnUpdateRunner implements Runnable {
	final RootController rootController;

	public RootControllerOnUpdateRunner( final RootController rootController ){
		this.rootController = rootController;
	}

	@Override
	public void run() {
		this.rootController.onUpdate();
	}
}
