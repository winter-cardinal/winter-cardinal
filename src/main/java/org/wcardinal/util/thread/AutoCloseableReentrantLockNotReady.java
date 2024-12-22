/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.thread;

import java.util.concurrent.TimeUnit;

import org.wcardinal.exception.NotReadyException;

public class AutoCloseableReentrantLockNotReady implements AutoCloseableReentrantLock {
	public static AutoCloseableReentrantLockNotReady INSTANCE = new AutoCloseableReentrantLockNotReady();

	@Override
	public AutoCloseableReentrantLock open() {
		throw new NotReadyException();
	}

	@Override
	public void close() {
		throw new NotReadyException();
	}

	@Override
	public void lock() {
		throw new NotReadyException();
	}

	@Override
	public boolean isLocked() {
		return false;
	}

	@Override
	public boolean tryLock() {
		throw new NotReadyException();
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
		throw new NotReadyException();
	}

	@Override
	public void unlock() {
		throw new NotReadyException();
	}

	@Override
	public int getHoldCount() {
		return 0;
	}

	@Override
	public boolean isHeldByCurrentThread() {
		return false;
	}
}
