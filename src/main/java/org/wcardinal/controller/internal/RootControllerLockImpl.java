/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

public class RootControllerLockImpl implements RootControllerLock {
	final long senderId;
	final RootController rootController;

	public RootControllerLockImpl( final long senderId, final RootController rootController ) {
		this.senderId = senderId;
		this.rootController = rootController;
	}

	@Override
	public void unlock(){
		rootController.unlockDynamicInfo( senderId );
	}

	@Override
	public long getId() {
		return senderId;
	}
}
