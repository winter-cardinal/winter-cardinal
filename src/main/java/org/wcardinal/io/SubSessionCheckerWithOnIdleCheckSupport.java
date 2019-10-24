/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

public class SubSessionCheckerWithOnIdleCheckSupport extends SubSessionCheckerWithDisconnectionSupport {
	public SubSessionCheckerWithOnIdleCheckSupport( final SubSession subSession ) {
		super( subSession );
	}

	long checkAndGetDelay() {
		final Long delay = subSession.getRootController().checkIdle( io );
		if( delay != null ) {
			return delay;
		} else if( 0 <= MAXIMUM_DISCONNECTION_TIME ) {
			return super.checkAndGetDelay();
		} else {
			if( io.getIdleTime() <= MAXIMUM_IDLE_TIME ) {
				return MAXIMUM_IDLE_TIME_QUARTER;
			} else {
				logger.info("{} {} Exceeded maximum idle time", io.getSessionId(), io.getSubSessionId());
				return -1;
			}
		}
	}
}
