/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import org.wcardinal.io.Session;
import org.wcardinal.io.SubSession;

public class SharedWebSocketIntercepter implements HandshakeInterceptor {
	protected final Logger logger = LoggerFactory.getLogger(SharedWebSocketIntercepter.class);

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		if( (request instanceof ServletServerHttpRequest) != true ) return false;
		final ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

		final HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
		if( httpServletRequest == null ) return false;

		final HttpSession httpSession = httpServletRequest.getSession(false);
		if( httpSession == null ) return false;

		final Session session = Session.get(httpSession);
		if( session == null ) return false;

		final List<String> ssids = getSubSessionIds( httpServletRequest );
		if( ssids.isEmpty() ) return false;

		for( final String ssid: ssids ){
			final SubSession subSession = session.getSubSession( ssid );
			if( subSession == null || subSession.isDestroyed() ) return false;
		}

		Session.set(session, attributes);
		attributes.put("ssids", ssids);
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Exception exception) {

	}

	List<String> getSubSessionIds( final HttpServletRequest request ) {
		final List<String> result = new ArrayList<>();

		final String ssids = request.getParameter( "ssids" );
		if( ssids != null ){
			for( final String ssid : ssids.split(",") ){
				result.add( ssid );
			}
		}

		return result;
	}
}
