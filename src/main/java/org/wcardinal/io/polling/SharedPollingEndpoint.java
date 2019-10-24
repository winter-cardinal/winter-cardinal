/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.polling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.CharSource;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.io.Endpoint;
import org.wcardinal.io.Session;
import org.wcardinal.io.SharedEndpointSession;
import org.wcardinal.io.SubSession;
import org.wcardinal.util.json.Json;

public class SharedPollingEndpoint extends AbstractPollingEndpoint {
	public SharedPollingEndpoint( final WCardinalConfiguration configuration ){
		super( configuration );
	}

	@Override
	protected ModelAndView handleRequestInternal(
			final HttpServletRequest request,
			final HttpServletResponse response ) throws Exception {

		final HttpSession httpSession = request.getSession(false);
		if( httpSession == null ) {
			response.sendError( HttpServletResponse.SC_BAD_REQUEST );
			return null;
		}

		final Session session = Session.get(httpSession);
		if( session == null ) {
			response.sendError( HttpServletResponse.SC_BAD_REQUEST );
			return null;
		}

		// Message handling
		if( request.getMethod().equalsIgnoreCase("POST") ) {
			onMessage( request, session );
			response.setStatus(HttpServletResponse.SC_OK);
			response.setHeader("Cache-Control", "max-age=0");
			response.setHeader("Content-Type", "application/json");
			response.setCharacterEncoding("UTF-8");
		} else {
			if( !updateEndpointSession( session, request ) ){
				response.sendError( HttpServletResponse.SC_BAD_REQUEST );
				return null;
			}
		}

		return null;
	}

	boolean updateEndpointSession(final Session session, final HttpServletRequest request ){
		// SSIDs
		final List<String> ssids = getSubSessionIds( request );
		if( ssids.isEmpty() ) return false;

		// Update an endpoint session
		final PollingEndpointSession endpointSession = new PollingEndpointSession(request, configuration);
		for( int i=0; i<ssids.size(); ++i ){
			final String ssid = ssids.get(i);
			final SubSession subSession = session.getSubSession(ssid);
			if( subSession != null ){
				final Endpoint endpoint = subSession.getEndpoint();
				if( endpoint != null){
					endpoint.set( new SharedEndpointSession( ssid, endpointSession ) );
				}
			}
		}

		return true;
	}

	boolean onMessage(final HttpServletRequest request, final Session session) throws Exception{
		try {
			final JsonNode node = Json.mapper.readTree( request.getReader() );
			if( node.isArray() != true || node.size() != 2 ) return false;

			// Message
			final JsonNode messageNode = node.get(1);
			if( messageNode.isTextual() != true ) return false;

			// Sub session
			final JsonNode subSessionIdNode = node.get(0);
			if( subSessionIdNode.isTextual() != true ) return false;
			final String subSessionId  = subSessionIdNode.asText();
			final SubSession subSession = session.getSubSession( subSessionId );
			if( subSession == null ) return false;

			// Endpoint
			final Endpoint endpoint = subSession.getEndpoint();
			if( endpoint == null ) return false;

			// Handle the message
			endpoint.onMessage( CharSource.wrap(messageNode.asText()).openBufferedStream() );
		} catch ( final IOException e ) {
			logger.debug("{} {} Malformed message {}", session.getId(), request.getReader(), e);
			return false;
		}

		return true;
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
