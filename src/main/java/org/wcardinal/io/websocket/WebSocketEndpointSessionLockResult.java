/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import org.wcardinal.io.EndpointSessionLockResult;

public class WebSocketEndpointSessionLockResult implements EndpointSessionLockResult {
	static final Logger logger = LoggerFactory.getLogger(WebSocketEndpointSessionLockResult.class);

	final WebSocketSession session;
	final ReentrantLock lock;

	WebSocketEndpointSessionLockResult( final WebSocketSession session, final ReentrantLock lock ) {
		this.session = session;
		this.lock = lock;
	}

	@Override
	public void cancel() {
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

	@Override
	public void send( final CharSequence message, final boolean isLast ) throws IOException {
		try {
			session.sendMessage(new TextMessage( message, isLast ));
		} catch ( final Exception e ) {
			if( e instanceof IOException && e.getCause() instanceof TimeoutException ) {
				logger.error("Message may too large: {} bytes", message.length(), e.getCause());
			}

			throw e;
		}
	}
}
