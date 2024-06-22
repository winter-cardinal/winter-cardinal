/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

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

public class WebSocketIntercepter implements HandshakeInterceptor {
	protected final Logger logger = LoggerFactory.getLogger(WebSocketIntercepter.class);

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

		final String ssid = getSubSessionId( httpServletRequest );
		if( ssid == null ) return false;

		final SubSession subSession = session.getSubSession( ssid );
		if( subSession == null || subSession.isDestroyed() ) return false;

		Session.set(session, attributes);
		attributes.put("ssid", ssid);
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Exception exception) {

	}

	protected String getSubSessionId( final HttpServletRequest request ) {
		final String[] ssids = request.getParameterMap().get("ssid");
		if( ssids != null && 0<ssids.length ){
			final String ssid = ssids[ 0 ];
			if( ssid != null ) {
				return ssid;
			}
		}
		return null;
	}
}
