/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import java.io.IOException;

public class EndpointSessionSenderWhole extends EndpointSessionSenderBase {
	final StringBuilder builder;

	public EndpointSessionSenderWhole( final EndpointSessionLockResult lockResult ) {
		super( lockResult );
		this.builder = new StringBuilder();
	}

	@Override
	public void send( final CharSequence message, final boolean isLast ) throws IOException {
		builder.append( message );
		if( isLast ) {
			lockResult.send( builder, true );
		}
	}
}
