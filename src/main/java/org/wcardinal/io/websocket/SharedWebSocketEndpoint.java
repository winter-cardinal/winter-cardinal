/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.CharSource;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.io.Endpoint;
import org.wcardinal.io.Session;
import org.wcardinal.io.SubSession;
import org.wcardinal.util.json.Json;

public class SharedWebSocketEndpoint extends AbstractWebSocketEndpoint {
	final Queue<Endpoint> waitingEndpoints = new ArrayDeque<>();

	@Autowired
	public SharedWebSocketEndpoint( final WCardinalConfiguration configuration ) {
		super( configuration );
	}

	@Override
	public void afterConnectionEstablished( final WebSocketSession webSocketSession ) throws Exception {
		final Session session = Session.get( webSocketSession );
		if( session != null ){
			for( final String ssid: getSubSessionIds( webSocketSession ) ) {
				final SubSession subSession = session.getSubSession( ssid );
				if( subSession != null ){
					this.webSocketSession.set( webSocketSession );
					webSocketSession.setBinaryMessageSizeLimit( MAXIMUM_BINARY_MESSAGE_SIZE );
					webSocketSession.setTextMessageSizeLimit( MAXIMUM_TEXT_MESSAGE_SIZE );

					final Endpoint endpoint = subSession.getEndpoint();
					if( endpoint.set( new SharedWebSocketEndpointSession( ssid, this, waitingEndpoints, endpoint ) ) ) return;
				}
			}
		}

		close( webSocketSession );
	}

	@SuppressWarnings("unchecked")
	List<String> getSubSessionIds( final WebSocketSession session ) {
		final Object subSessionIdsObject = session.getAttributes().get( "ssids" );
		if( subSessionIdsObject instanceof List<?> ) return (List<String>) subSessionIdsObject;
		return Collections.emptyList();
	}

	@Override
	public void afterConnectionClosed( final WebSocketSession webSocketSession, final CloseStatus status ) throws Exception {
		this.webSocketSession.set( null );

		final Session session = Session.get( webSocketSession );
		if( session != null ){
			for( final SubSession subSession: session.getSubSessions() ) {
				final Endpoint endpoint = subSession.getEndpoint();
				if( endpoint != null ) {
					endpoint.unset( this );
				}
			}
		}
	}

	@Override
	public void handleTextMessage( final WebSocketSession webSocketSession, final TextMessage message ) throws Exception {
		final String payload = message.getPayload();
		if( message.isLast() ){
			final StringBuilder partials;
			synchronized( this.partials ) {
				partials = this.partials.set( null );
				if( partials != null ) {
					partials.append( payload );
				}
			}
			try{
				final Session session = Session.get( this.webSocketSession.get());
				if( partials == null ) {
					onMessages( payload, session );
				} else {
					onMessages( partials, session );
				}
			}catch(final Exception e){
				logger.debug( "Failed to handle a message.", e );
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

	void onMessages( final StringBuilder messages, final Session session ) throws IOException {
		final JsonNode node = Json.mapper.readTree( CharSource.wrap(messages).openBufferedStream() );
		onMessages( node, session );
	}

	void onMessages( final String messages, final Session session ) throws IOException {
		final JsonNode node = Json.mapper.readTree( messages );
		onMessages( node, session );
	}

	void onMessages( final JsonNode node, final Session session ) throws IOException {
		if( node.isArray() != true || node.size() != 2 ) return;

		// Sub session
		final JsonNode subSessionIdNode = node.get(0);
		if( subSessionIdNode.isTextual() != true ) return;
		final String subSessionId  = subSessionIdNode.asText();
		final SubSession subSession = session.getSubSession( subSessionId );
		if( subSession == null ) return;

		// Endpoint
		final Endpoint endpoint = subSession.getEndpoint();
		if(endpoint == null) return;

		// Handle message
		final JsonNode messageNode = node.get(1);
		if( messageNode.isIntegralNumber() ){
			endpoint.set( new SharedWebSocketEndpointSession( subSessionId, this, waitingEndpoints, endpoint ) );
		} else {
			final String message = messageNode.asText();
			if( message.length() <= 2 ) {
				endpoint.touch();
			} else {
				endpoint.onMessage( message );
			}
		}
	}
}
