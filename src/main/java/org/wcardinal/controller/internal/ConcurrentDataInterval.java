/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.concurrent.atomic.AtomicReference;

import org.wcardinal.util.reflection.LockRequirements;
import org.wcardinal.util.reflection.TrackingData;
import org.wcardinal.util.reflection.VoidTypedParametrizedMethods;
import org.wcardinal.util.thread.Unlocker;

class ConcurrentDataInterval extends ConcurrentData {
	final String name;
	final VoidTypedParametrizedMethods methods;
	final AtomicReference<TrackingData> trackingData;
	final long interval;
	final Object[] parameters;

	public ConcurrentDataInterval(
		final Controller controller,
		final String name,
		final VoidTypedParametrizedMethods methods,
		final TrackingData trackingData,
		final long interval,
		final Object[] parameters
	) {
		super( controller );

		this.name = name;
		this.methods = methods;
		this.trackingData = ( trackingData == null ? null : new AtomicReference<TrackingData>( trackingData ) );
		this.interval = interval;
		this.parameters = parameters;
	}

	@Override
	public void run( final long id, final Runnable runnable ) {
		final TrackingData trackingData = ( this.trackingData != null ? this.trackingData.get() : null );
		final Object instance = controller.getInstance();

		controller.setRequestId( id );
		methods.call( controller, name, trackingData, null, LockRequirements.NOT_REQUIRED_OR_UNSPECIFIED, instance, parameters );

		if( methods.containsLockRequired() || methods.containsLockRequired( name ) ) {
			try( final Unlocker unlocker = controller.lock() ) {
				methods.call( controller, name, trackingData, null, LockRequirements.REQUIRED, controller.instance, parameters );
			}
		}

		controller.setRequestId( null );
		if( this.trackingData != null ) {
			this.trackingData.set( methods.getTrackingData( name, controller, instance) );
		}

		schedule( runnable, interval );
	}
}
