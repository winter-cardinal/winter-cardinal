/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

class ConcurrentDataTimeoutCallable<V> extends ConcurrentData {
	final Callable<V> callable;
	final AtomicReference<V> result;

	public ConcurrentDataTimeoutCallable(
		final Controller controller,
		final Callable<V> callable,
		AtomicReference<V> result
	) {
		super( controller );
		this.callable = callable;
		this.result = result;
	}

	@Override
	void run( final long id, final Runnable runnable ) {
		try {
			result.set( callable.call() );
		} catch ( final Exception e ) {
			throw new TimeoutExecutionException( e );
		}
	}
}
