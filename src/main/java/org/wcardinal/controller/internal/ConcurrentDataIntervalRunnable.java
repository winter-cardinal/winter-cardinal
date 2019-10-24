/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

class ConcurrentDataIntervalRunnable extends ConcurrentData {
	final Runnable runnable;
	final long interval;

	public ConcurrentDataIntervalRunnable(
		final Controller controller,
		final Runnable runnable,
		final long interval
	) {
		super( controller );
		this.runnable = runnable;
		this.interval = interval;
	}

	@Override
	void run( final long id, final Runnable runnable ) {
		controller.setRequestId( id );
		try {
			this.runnable.run();
		} finally {
			controller.setRequestId( null );
			schedule( runnable, interval );
		}
	}
}
