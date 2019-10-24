/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.io.EndpointSessionLockResult;
import org.wcardinal.util.Reference;

public class AbstractWebSocketEndpoint extends TextWebSocketHandler implements org.wcardinal.io.EndpointSession {
	final int MAXIMUM_BINARY_MESSAGE_SIZE;
	final int MAXIMUM_TEXT_MESSAGE_SIZE;

	final Logger logger = LoggerFactory.getLogger(AbstractWebSocketEndpoint.class);

	final AtomicReference<WebSocketSession> webSocketSession = new AtomicReference<>(null);
	final Reference<StringBuilder> partials = new Reference<>();

	final ReentrantLock lock = new ReentrantLock();

	@Autowired
	public AbstractWebSocketEndpoint( final WCardinalConfiguration configuration ) {
		super();
		MAXIMUM_BINARY_MESSAGE_SIZE = configuration.getMaximumBinaryMessageSize();
		MAXIMUM_TEXT_MESSAGE_SIZE = configuration.getMaximumTextMessageSize();
	}

	void close( final WebSocketSession session ){
		if( session != null ){
			try {
				session.close();
			} catch (final Exception e) {
				logger.error("Failed to close a connection", e);
			}
		}
	}

	@Override
	public void close() {
		close( webSocketSession.getAndSet(null) );
	}

	@Override
	public boolean supportsPartialMessages(){
		return true;
	}

	@Override
	public EndpointSessionLockResult tryLock() {
		final WebSocketSession session = this.webSocketSession.get();
		if( session != null && lock.tryLock() ) {
			return new WebSocketEndpointSessionLockResult( session, lock );
		}
		return null;
	}
}
