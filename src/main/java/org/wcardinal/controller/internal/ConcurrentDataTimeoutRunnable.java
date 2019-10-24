/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

class ConcurrentDataTimeoutRunnable extends ConcurrentData {
	final Runnable runnable;

	public ConcurrentDataTimeoutRunnable(
		final Controller controller,
		final Runnable runnable
	) {
		super( controller );
		this.runnable = runnable;
	}

	@Override
	void run( final long id, final Runnable runnable ) {
		this.runnable.run();
	}
}
