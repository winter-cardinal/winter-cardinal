/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.wcardinal.controller.TimeoutFuture;

class TimeoutFutureImpl<T> implements TimeoutFuture<T> {
	final Controller controller;
	final ScheduledFuture<?> future;
	final AtomicReference<T> result;
	final long id;

	public TimeoutFutureImpl( final Controller controller, final ScheduledFuture<?> future, final AtomicReference<T> result, final long id ){
		this.controller = controller;
		this.future = future;
		this.result = result;
		this.id = id;
	}

	@Override
	public boolean cancel( final boolean mayInterruptIfRunning ) {
		controller.cancel( this.id );
		return future.cancel( mayInterruptIfRunning );
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		try {
			future.get();
		} catch( final ExecutionException e ) {
			final Throwable cause = e.getCause();
			if( cause instanceof TimeoutExecutionException ) {
				throw new ExecutionException( cause.getCause() );
			} else {
				throw e;
			}
		}
		return result.get();
	}

	@Override
	public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		try {
			future.get( timeout, unit );
		} catch( final ExecutionException e ) {
			final Throwable cause = e.getCause();
			if( cause instanceof TimeoutExecutionException ) {
				throw new ExecutionException( cause.getCause() );
			} else {
				throw e;
			}
		}
		return result.get();
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

	@Override
	public long getDelay( final TimeUnit unit ) {
		return future.getDelay( unit );
	}

	@Override
	public int compareTo( final Delayed o ) {
		return future.compareTo( o );
	}

	@Override
	public long getId(){
		return id;
	}
}
