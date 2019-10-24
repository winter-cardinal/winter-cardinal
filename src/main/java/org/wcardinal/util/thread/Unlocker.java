/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.thread;


public class Unlocker implements AutoCloseable {
	final Unlockable unlockable;

	public Unlocker( final Unlockable unlockable ){
		this.unlockable = unlockable;
	}

	@Override
	public void close() {
		unlockable.unlock();
	}
}
