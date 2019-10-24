/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.wcardinal.util.reflection.LockRequirements;
import org.wcardinal.util.reflection.TrackingData;
import org.wcardinal.util.reflection.VoidTypedParametrizedMethods;
import org.wcardinal.util.thread.Unlocker;

class ConcurrentDataTimeout extends ConcurrentData {
	final String name;
	final VoidTypedParametrizedMethods methods;
	final TrackingData trackingData;
	final Object[] parameters;

	public ConcurrentDataTimeout(
		final String name,
		final Controller controller,
		final VoidTypedParametrizedMethods methods,
		final TrackingData trackingData,
		final Object[] parameters
	) {
		super( controller );
		this.name = name;
		this.methods = methods;
		this.trackingData = trackingData;
		this.parameters = parameters;
	}

	@Override
	void run( final long id, final Runnable runnable ) {
		final Object instance = controller.getInstance();

		methods.call( controller, name, trackingData, null, LockRequirements.NOT_REQUIRED_OR_UNSPECIFIED, instance, parameters);

		if( methods.containsLockRequired() || methods.containsLockRequired( name ) ) {
			try( final Unlocker unlocker = controller.lock() ) {
				methods.call( controller, name, trackingData, null, LockRequirements.REQUIRED, instance, parameters );
			}
		}
	}
}
