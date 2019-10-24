/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

public class SharedEndpointSession implements EndpointSession {
	protected final EndpointSession endpointSession;
	protected final String subSessionId;

	public SharedEndpointSession( final String subSessionId, final EndpointSession endpointSession ) {
		this.endpointSession = endpointSession;
		this.subSessionId = subSessionId;
	}

	@Override
	public void close() {
		endpointSession.close();
	}

	@Override
	public EndpointSessionLockResult tryLock() {
		final EndpointSessionLockResult result = endpointSession.tryLock();
		if( result != null ) {
			return new SharedEndpointSessionLockResult( subSessionId, result );
		} else {
			return null;
		}
	}
}
