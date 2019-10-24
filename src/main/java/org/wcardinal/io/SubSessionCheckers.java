/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

public class SubSessionCheckers {
	static SubSessionChecker create( final SubSession subSession ) {
		if( subSession.getRootController().hasOnIdleCheckMethods() ) {
			return new SubSessionCheckerWithOnIdleCheckSupport( subSession );
		} else if( 0 <= subSession.getConfiguration().getMaximumDisconnectionTime() ) {
			return new SubSessionCheckerWithDisconnectionSupport( subSession );
		} else {
			return new SubSessionCheckerDefault( subSession );
		}
	}
}
