/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import java.util.concurrent.TimeUnit;

import org.wcardinal.util.thread.Unlockable;
import org.wcardinal.util.thread.Unlocker;

/**
 * Provides lock/unlock functions.
 */
public interface SLockable extends Unlockable {
	/**
	 * Locks this instance.
	 *
	 * @return {@link org.wcardinal.util.thread.Unlocker Unlocker} instance for unlocking this lock
	 */
	Unlocker lock();

	/**
	 * Tries to lock this instance.
	 *
	 * @return true if succeeded
	 */
	boolean tryLock();

	/**
	 * Tries to lock this instance.
	 *
	 * @param timeout the timeout for this trial
	 * @param unit the unit of the timeout
	 * @return true if succeeded
	 */
	boolean tryLock(final long timeout, final TimeUnit unit);

	/**
	 * Returns true if this instance is locked.
	 *
	 * @return true if this instance is locked.
	 */
	boolean isLocked();

	/**
	 * Unlocks this instance.
	 */
	@Override
	void unlock();

	/**
	 * Unlocks this instance.
	 *
	 * @param origin the origin of the event
	 */
	void unlock( Object origin );
}
