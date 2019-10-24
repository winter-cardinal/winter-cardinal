/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import org.wcardinal.io.message.ReceivedRequestMessage;

public class EndpointOnMessageRunner implements Runnable {
	private Endpoint endpoint;
	private ReceivedRequestMessage message;

	public EndpointOnMessageRunner( final Endpoint endpoint, final ReceivedRequestMessage message ){
		this.endpoint = endpoint;
		this.message = message;
	}

	@Override
	public void run() {
		endpoint.onMessage( message );
	}
}
