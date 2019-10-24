/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

import java.util.Queue;

import org.wcardinal.io.Endpoint;
import org.wcardinal.io.EndpointSession;
import org.wcardinal.io.EndpointSessionLockResult;
import org.wcardinal.io.SharedEndpointSession;

public class SharedWebSocketEndpointSession extends SharedEndpointSession {
	protected final Queue<Endpoint> waitingEndpoints;
	protected final Endpoint endpoint;

	public SharedWebSocketEndpointSession( final String subSessionId, final EndpointSession endpointSession, Queue<Endpoint> waitingEndpoints, final Endpoint endpoint ) {
		super( subSessionId, endpointSession );
		this.waitingEndpoints = waitingEndpoints;
		this.endpoint = endpoint;
	}

	@Override
	public void close() {
		endpointSession.close();
	}

	@Override
	public EndpointSessionLockResult tryLock() {
		synchronized( waitingEndpoints ) {
			waitingEndpoints.add( endpoint );
		}

		final EndpointSessionLockResult result = endpointSession.tryLock();
		if( result != null ) {
			synchronized( waitingEndpoints ) {
				while( waitingEndpoints.peek() == endpoint ) {
					waitingEndpoints.poll();
				}
			}
			return new SharedWebSocketEndpointSessionLockResult( subSessionId, result, waitingEndpoints );
		} else {
			return null;
		}
	}
}
