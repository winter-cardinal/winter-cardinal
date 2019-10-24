/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.thread;

import java.util.concurrent.locks.ReentrantLock;

public class AutoCloseableReentrantLock extends ReentrantLock implements AutoCloseable {
	private static final long serialVersionUID = -4018664219297885401L;

	public AutoCloseableReentrantLock open() {
		lock();
		return this;
	}

	@Override
	public void close() {
		unlock();
	}
}
