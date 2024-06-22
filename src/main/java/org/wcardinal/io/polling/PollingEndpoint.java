/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.polling;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.io.Endpoint;
import org.wcardinal.io.EndpointSession;
import org.wcardinal.io.Session;
import org.wcardinal.io.SubSession;

public class PollingEndpoint extends AbstractPollingEndpoint {
	public PollingEndpoint( final WCardinalConfiguration configuration ){
		super( configuration );
	}

	@Override
	protected ModelAndView handleRequestInternal(
			final HttpServletRequest request,
			final HttpServletResponse response ) throws Exception {

		final String ssid = getSubSessionId( request );
		if( ssid == null ) {
			response.sendError( HttpServletResponse.SC_BAD_REQUEST );
			return null;
		}

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

		// Get a communicator endpoint
		final SubSession subSession = session.getSubSession( ssid );
		if( subSession == null ) {
			response.sendError( HttpServletResponse.SC_BAD_REQUEST );
			return null;
		}

		// Endpoint
		final Endpoint endpoint = subSession.getEndpoint();

		// Message handling
		if( request.getMethod().equalsIgnoreCase("POST") ) {
			endpoint.onMessage( request.getReader() );
			response.setStatus(HttpServletResponse.SC_OK);
			response.setHeader("Cache-Control", "max-age=0");
			response.setHeader("Content-Type", "application/json");
			response.setCharacterEncoding("UTF-8");
		} else {
			final EndpointSession endpointSession = new PollingEndpointSession( request, configuration );
			if( endpoint.set( endpointSession ) != true ){
				response.sendError( HttpServletResponse.SC_BAD_REQUEST );
				return null;
			}
		}

		return null;
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
