/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.concurrent.TimeUnit;

import org.wcardinal.controller.internal.ControllerDynamicInfoHandler;
import org.wcardinal.util.thread.Unlocker;

public interface SContainerParent extends SParent {
	void put(Object origin, String name, SBase<?> sbase);
	void put(ControllerDynamicInfoHandler handler);
	boolean isShared();

	Unlocker lock();
	boolean tryLock();
	boolean tryLock(long timeout, TimeUnit unit);
	boolean isLocked();
	boolean isLockedByCurrentThread();
	void unlock();
}
