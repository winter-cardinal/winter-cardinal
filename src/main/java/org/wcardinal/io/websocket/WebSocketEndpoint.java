/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.common.io.*;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.io.SubSession;
import org.wcardinal.io.Endpoint;
import org.wcardinal.io.Session;

public class WebSocketEndpoint extends AbstractWebSocketEndpoint {
	final AtomicReference<Endpoint> endpoint = new AtomicReference<>(null);

	@Autowired
	public WebSocketEndpoint( final WCardinalConfiguration configuration ) {
		super( configuration );
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {
		final String ssid = getSubSessionId( webSocketSession );
		final Session session = Session.get( webSocketSession );

		// Get a communicator endpoint
		if( ssid != null && session != null ){
			final SubSession subSession = session.getSubSession( ssid );
			if( subSession != null ){
				this.webSocketSession.set(webSocketSession);
				webSocketSession.setBinaryMessageSizeLimit(MAXIMUM_BINARY_MESSAGE_SIZE);
				webSocketSession.setTextMessageSizeLimit(MAXIMUM_TEXT_MESSAGE_SIZE);

				final Endpoint endpoint = subSession.getEndpoint();
				this.endpoint.set( endpoint );
				if( endpoint.set( this ) ) return;
			}
		}

		// Close
		close( webSocketSession );
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession webSocketSession, final CloseStatus status) throws Exception {
		this.webSocketSession.set(null);
		final Endpoint endpoint = this.endpoint.getAndSet(null);
		if( endpoint != null ) {
			endpoint.unset( this );
		}
	}

	@Override
	public void handleTextMessage(final WebSocketSession webSocketSession, final TextMessage message) throws Exception {
		final String payload = message.getPayload();
		if( message.isLast() ){
			final Endpoint endpoint = this.endpoint.get();
			if( endpoint != null ){
				if( payload.length() <= 2 ) {
					endpoint.touch();
				} else {
					final StringBuilder partials;
					synchronized( this.partials ) {
						partials = this.partials.set( null );
						if( partials != null ) {
							partials.append( payload );
						}
					}
					if( partials == null ) {
						endpoint.onMessage( payload );
					} else {
						endpoint.onMessage( CharSource.wrap(partials).openBufferedStream() );
					}
				}
			}
		} else {
			synchronized( this.partials ){
				StringBuilder partials = this.partials.get();
				if( partials == null ) {
					partials = new StringBuilder( payload );
					this.partials.set( partials );
				} else {
					partials.append( payload );
				}
			}
		}
	}

	String getSubSessionId( final WebSocketSession webSocketSession ) {
		final Object subSessionIdObject = webSocketSession.getAttributes().get("ssid");
		if( subSessionIdObject instanceof String ) return (String) subSessionIdObject;
		return null;
	}

	@Override
	public void close() {
		super.close();
		endpoint.set(null);
	}
}
