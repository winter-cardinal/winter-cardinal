/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

import java.util.Queue;

import org.wcardinal.io.Endpoint;
import org.wcardinal.io.EndpointSessionLockResult;
import org.wcardinal.io.SharedEndpointSessionLockResult;

public class SharedWebSocketEndpointSessionLockResult extends SharedEndpointSessionLockResult {
	final Queue<Endpoint> waitingEndpoints;

	public SharedWebSocketEndpointSessionLockResult( final String subSessionId, final EndpointSessionLockResult lockResult, final Queue<Endpoint> endpoints ){
		super( subSessionId, lockResult );
		this.waitingEndpoints = endpoints;
	}

	@Override
	public void unlock() {
		super.unlock();

		while( true ) {
			Endpoint endpoint = null;
			synchronized( waitingEndpoints ) {
				endpoint = waitingEndpoints.poll();
			}
			if( endpoint == null ) break;
			endpoint.flush();
		}
	}
}
