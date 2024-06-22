/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.io.Session;

public class ControllerSessionListener implements HttpSessionListener{
	static final Logger logger = LoggerFactory.getLogger(ControllerSessionListener.class);

	static public void sessionCreated( final String sessionId ){
		logger.debug("Created HTTP session {}", sessionId);
	}

	static public void sessionDestroyed( final String sessionId ){
		final Session session = Session.remove( sessionId );
		if( session != null ) session.destroy();
		logger.debug("Destroyed HTTP session {}", sessionId);
	}


	@Override
	public void sessionCreated(HttpSessionEvent se){
		sessionCreated( se.getSession().getId() );
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se){
		sessionDestroyed( se.getSession().getId() );
	}
}
