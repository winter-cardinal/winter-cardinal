/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.thread;

import java.util.concurrent.TimeUnit;

public interface AutoCloseableReentrantLock extends AutoCloseable {
	AutoCloseableReentrantLock open();
	void lock();
	boolean isLocked();
	boolean tryLock();
	boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException;
	void unlock();
	void close();
	int getHoldCount();
	boolean isHeldByCurrentThread();
}
