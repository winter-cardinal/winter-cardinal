/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import org.wcardinal.controller.annotation.Debounced;

public class MethodWrapperDecoratorDebounced<T> extends MethodWrapperDecoratorLocked<T> {
	final boolean leading;
	final boolean trailing;
	final long maxInterval;
	final long interval;

	static class Argument {
		final MethodContainer container;
		final TrackingData trackingData;
		final MethodHook hook;
		final Object instance;
		final Object[] parameters;

		public Argument( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Object instance, final Object[] parameters ){
			this.container = container;
			this.trackingData = trackingData;
			this.hook = hook;
			this.instance = instance;
			this.parameters = parameters;
		}
	}

	static class Result<T> {
		boolean hasResult = false;
		MethodResult<T> result = null;

		public Result(){ }

		public synchronized MethodResult<T> set( final MethodResult<T> result ) {
			this.result = result;
			this.hasResult = true;
			return this.result;
		}

		public MethodResult<T> get(){
			while( true ) {
				synchronized( this ){
					if( hasResult ) {
						return result;
					}
				}

				Thread.yield();
			}
		}
	}

	static class Data<T> extends LockableData {
		long id = -1;
		long calledId = -1;

		boolean isLocked = true;

		long timestamp = 0;
		long trailingTimestamp = -1;
		long maxIntervalTimestamp = -1;
		Long requestId = null;

		Argument argument = null;
		Result<T> result = null;
	}

	public MethodWrapperDecoratorDebounced( final MethodWrapper<T> wrapper, final Debounced debounced ){
		this( wrapper, debounced.interval(), debounced.leading(), debounced.trailing(), debounced.maxInterval() );
	}

	public MethodWrapperDecoratorDebounced( final MethodWrapper<T> wrapper, final long interval,
			final boolean leading, final boolean trailing, final long maxInterval ){
		super( wrapper );
		this.interval = interval;
		this.leading = leading;
		this.trailing = trailing;
		this.maxInterval = maxInterval;
	}

	@Override
	public void init( final MethodContainer container ){
		container.putWorkingData( this, new Data<T>() );
	}

	@Override
	public MethodResult<T> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Object instance, final Object[] parameters ) {
		if( container == null ) {
			return wrapper.callUndecorated(null, trackingData, hook, instance, parameters);
		}

		final Data<T> data = container.<Data<T>>getWorkingData( this );
		if( data == null ) return null;

		boolean doCall = false;
		Result<T> result = null;
		Argument argument = null;

		synchronized( data ) {
			argument = new Argument( container, trackingData, hook, instance, parameters );
			final boolean isLocked = container.isLockedByCurrentThread();
			final Long requestId = container.getRequestId();
			final long now = System.currentTimeMillis();
			if( data.trailingTimestamp < 0 ){
				data.trailingTimestamp = now + interval;
				if( 0 <= maxInterval ) {
					data.maxIntervalTimestamp = now + maxInterval;
				} else {
					data.maxIntervalTimestamp = Long.MAX_VALUE;
				}
				data.argument = argument;
				data.isLocked = isLocked;
				data.requestId = requestId;
				data.id = 0;
				if( leading ){
					data.calledId = 0;
					data.timestamp = now;
					result = new Result<T>();
					data.result = result;
					doCall = true;
				} else {
					data.calledId = -1;
				}
				trailing( argument.container, data );
			} else {
				data.argument = argument;
				data.isLocked = isLocked;
				data.requestId = requestId;
				data.id += 1;
				result = data.result;
				doCall = false;
			}
		}

		if( doCall ) {
			return result.set( wrapper.callUndecorated(container, argument.trackingData, argument.hook, argument.instance, argument.parameters) );
		} else {
			return result.get();
		}
	}

	void trailing( final MethodContainer container, final Data<T> data ){
		final Runnable runnable = new Runnable(){
			@Override
			public void run() {
				doTrailing( container, data );
			}
		};

		final long now = System.currentTimeMillis();
		final long timestamp = Math.min(data.trailingTimestamp, data.maxIntervalTimestamp);
		container.getScheduler().schedule(runnable, Math.max(0, timestamp-now));
	}

	void doTrailing( final MethodContainer container, final Data<T> data ){
		Result<T> result = null;
		Argument argument = null;
		boolean isLocked = false;
		Long requestId = null;

		synchronized( data ) {
			argument = data.argument;
			final long now = System.currentTimeMillis();
			isLocked = data.isLocked;
			requestId = data.requestId;
			if( 0 <= now - data.trailingTimestamp ){
				data.argument = null;
				data.isLocked = true;
				data.requestId = null;
				if( trailing && data.id != data.calledId ) {
					data.timestamp = now;
					result = new Result<T>();
					data.result = result;
				}
				data.id = -1;
				data.calledId = -1;
				data.trailingTimestamp = -1;
				data.maxIntervalTimestamp = -1;
			} else if( 0 <= now - data.maxIntervalTimestamp ){
				if( 0 <= maxInterval ) {
					data.maxIntervalTimestamp = now + maxInterval;
				} else {
					data.maxIntervalTimestamp = Long.MAX_VALUE;
				}
				data.timestamp = now;
				data.calledId = data.id;
				result = new Result<T>();
				data.result = result;
				trailing( container, data );
			} else {
				trailing( container, data );
			}
		}

		if( result != null ) {
			result.set( call( container, argument.trackingData, argument.hook, requestId, isLocked, argument.instance, argument.parameters ) );
		}
	}

	public MethodResult<T> call( final MethodContainer container, final TrackingData trackingData, final MethodHook hook, final Long requestId, final boolean isLocked, final Object instance, final Object[] parameters ) {
		if( requestId != null ) {
			container.setRequestId( requestId );
			try {
				return call(container, trackingData, hook, isLocked, instance, parameters);
			} finally {
				container.setRequestId( null );
			}
		} else {
			return call(container, trackingData, hook, isLocked, instance, parameters);
		}
	}
}
