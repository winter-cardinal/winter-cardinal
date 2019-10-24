/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.controller.ControllerIo;

public class SubSessionCheckerWithDisconnectionSupport implements SubSessionChecker, SubSessionCheckerParent {
	final Logger logger = LoggerFactory.getLogger(SubSession.class);

	final long MAXIMUM_IDLE_TIME;
	final long MAXIMUM_IDLE_TIME_QUARTER;
	final long MAXIMUM_DISCONNECTION_TIME;
	final long MAXIMUM_DISCONNECTION_TIME_QUARTER;

	final SubSession subSession;
	final AtomicReference<SubSessionCheckerTask> task = new AtomicReference<>( null );
	final ControllerIo io;

	public SubSessionCheckerWithDisconnectionSupport( final SubSession subSession ) {
		final WCardinalConfiguration configuration = subSession.getConfiguration();
		MAXIMUM_IDLE_TIME = configuration.getMaximumIdleTime();
		MAXIMUM_IDLE_TIME_QUARTER = (MAXIMUM_IDLE_TIME >>> 2);
		MAXIMUM_DISCONNECTION_TIME = configuration.getMaximumDisconnectionTime();
		MAXIMUM_DISCONNECTION_TIME_QUARTER = (MAXIMUM_DISCONNECTION_TIME >>> 2);

		this.subSession = subSession;
		this.io = subSession.getEndpoint();
	}

	@Override
	public void start() {
		check();
	}

	@Override
	public void stop() {
		setTask( null );
	}

	private SubSessionCheckerTask setTask( final SubSessionCheckerTask newTask ) {
		final SubSessionCheckerTask oldTask = this.task.getAndSet( newTask );
		if( oldTask != null ) {
			oldTask.cancel();
		}
		return oldTask;
	}

	private SubSessionCheckerTask makeTask() {
		return new SubSessionCheckerTask( this );
	}

	@Override
	public void check() {
		if( ! subSession.isDestroyed() ) {
			final SubSessionCheckerTask task = makeTask();
			setTask( task );
			subSession.getScheduler().execute( task );
		}
	}

	@Override
	public void check( final SubSessionCheckerTask task ) {
		if( ! subSession.isDestroyed() ) {
			final long delay = checkAndGetDelay();
			if( 0 <= delay ) {
				subSession.getScheduler().schedule( task, delay );
			} else {
				subSession.destroy();
			}
		}
	}

	long checkAndGetDelay() {
		final long idleTime = io.getIdleTime();
		if( io.hadConnection() && ! io.hasConnection() ) {
			if( idleTime <= MAXIMUM_DISCONNECTION_TIME ) {
				return MAXIMUM_DISCONNECTION_TIME_QUARTER;
			} else {
				logger.info("{} {} Exceeded maximum disconnection time", io.getSessionId(), io.getSubSessionId());
				return -1;
			}
		} else {
			if( idleTime <= MAXIMUM_IDLE_TIME ) {
				return MAXIMUM_IDLE_TIME_QUARTER;
			} else {
				if( idleTime <= MAXIMUM_DISCONNECTION_TIME ) {
					return MAXIMUM_DISCONNECTION_TIME_QUARTER;
				} else {
					logger.info("{} {} Exceeded maximum disconnection time", io.getSessionId(), io.getSubSessionId());
					return -1;
				}
			}
		}
	}
}
