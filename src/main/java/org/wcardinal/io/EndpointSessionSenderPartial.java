/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import java.io.IOException;

public class EndpointSessionSenderPartial extends EndpointSessionSenderBase {
	public EndpointSessionSenderPartial( final EndpointSessionLockResult session ) {
		super( session );
	}

	@Override
	public void send( final CharSequence message, final boolean isLast ) throws IOException {
		lockResult.send( message, isLast );
	}
}
