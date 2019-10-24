/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.controller.ControllerIo;

public class SubSessionCheckerDefault implements SubSessionChecker, Runnable {
	final Logger logger = LoggerFactory.getLogger(SubSession.class);

	final long MAXIMUM_IDLE_TIME;
	final long MAXIMUM_IDLE_TIME_QUARTER;

	final SubSession subSession;
	final ControllerIo io;

	public SubSessionCheckerDefault( final SubSession subSession ) {
		MAXIMUM_IDLE_TIME = subSession.getConfiguration().getMaximumIdleTime();
		MAXIMUM_IDLE_TIME_QUARTER = (MAXIMUM_IDLE_TIME >>> 2);

		this.subSession = subSession;
		this.io = subSession.getEndpoint();
	}

	@Override
	public void start() {
		subSession.getScheduler().execute( this );
	}

	@Override
	public void stop() {
		// DO NOTHING
	}

	@Override
	public void check() {
		// DO NOTHING
	}

	@Override
	public void run() {
		if( ! subSession.isDestroyed() ) {
			final long delay = checkAndGetDelay();
			if( 0 <= delay ) {
				subSession.getScheduler().schedule( this, delay );
			} else {
				subSession.destroy();
			}
		}
	}

	long checkAndGetDelay() {
		if( io.getIdleTime() <= MAXIMUM_IDLE_TIME ) {
			return MAXIMUM_IDLE_TIME_QUARTER;
		} else {
			logger.info("{} {} Exceeded maximum idle time", io.getSessionId(), io.getSubSessionId());
			return -1;
		}
	}
}
