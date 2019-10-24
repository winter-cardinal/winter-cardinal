/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

abstract class ConcurrentData {
	final Controller controller;

	public ConcurrentData( final Controller controller ) {
		this.controller = controller;
	}

	abstract void run( long id, Runnable runnable );

	void schedule( Runnable runnable, long delay ) {
		controller.getScheduler().schedule( runnable, delay );
	}
}
