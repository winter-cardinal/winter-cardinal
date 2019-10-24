/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import org.wcardinal.util.thread.Scheduler;
import org.wcardinal.util.thread.Unlocker;

public interface MethodContainer {
	<V> V getWorkingData( Object key );
	void putWorkingData( final Object key, final Object data );

	TrackingIds getTrackingIds();
	void setTrackingIds( final TrackingIds ids );

	Unlocker lock();
	void unlock();
	boolean isLockedByCurrentThread();

	Long getRequestId();
	void setRequestId( final Long id );

	Scheduler getScheduler();

	<T> void handle( final MethodResult<T> result );
}
