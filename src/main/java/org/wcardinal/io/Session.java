/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import java.io.Serializable;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.WebSocketSession;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.controller.internal.ControllerFactory;
import org.wcardinal.util.doc.ThreadSafe;
import org.wcardinal.util.thread.Scheduler;

/**
 * Central class of sessions.
 */
public class Session implements Serializable, Runnable {
	private static final long serialVersionUID = -5906639040002547127L;

	final Logger logger = LoggerFactory.getLogger(Session.class);

	final static String ATTRIBUTE_NAME = "wcardinal-session";
	final static Map<String, Session> sessionIdToSession = new HashMap<String, Session>();

	boolean destroyed = false;

	Map<String, SubSession> subSessionIdToSubSession;

	final String sessionId;
	final ApplicationContext context;
	final Scheduler scheduler;
	final WCardinalConfiguration configuration;
	final long maximumIdleTime;

	protected Session( final String sessionId, final ApplicationContext context,
			final WCardinalConfiguration configuration, final Scheduler scheduler ){
		this.sessionId = sessionId;
		this.context = context;
		this.configuration = configuration;
		this.scheduler = scheduler;
		this.subSessionIdToSubSession = new HashMap<String, SubSession>();
		this.maximumIdleTime = configuration.getMaximumIdleTime();

		logger.info("{} Created", sessionId);
		scheduler.execute( this );
	}

	@Override
	public String toString(){
		return "Session("+sessionId+")";
	}

	public synchronized SessionResult createSubSession( final ControllerFactory factory, final String remoteAddress, final Principal principal,
			final Map<String, String[]> parameters, final List<Locale> locales, final HttpServletRequest request ){
		if( destroyed == true ) return null;

		while( true ){
			final String ssid = Long.toString(Math.round(Math.random() * Long.MAX_VALUE), 32);
			final SubSession subSession = subSessionIdToSubSession.get(ssid);
			if( subSession == null ){
				final SubSession result = new SubSession( sessionId, ssid, configuration, scheduler, factory, remoteAddress, principal, parameters, locales, request );
				subSessionIdToSubSession.put(ssid, result);
				cleanup();
				return new SessionResult( ssid, result, true );
			}
		}
	}

	public synchronized SubSession getSubSession( final String subSessionId ){
		if( destroyed == true ) return null;
		return subSessionIdToSubSession.get(subSessionId);
	}

	public synchronized List<SubSession> getSubSessions(){
		if( destroyed == true ) return Collections.emptyList();
		return new ArrayList<SubSession>( subSessionIdToSubSession.values() );
	}

	public String getId(){
		return sessionId;
	}

	public synchronized SessionResult getSubSession( final ControllerFactory factory, final String subSessionId, final String remoteAddress,
			final boolean create, final Principal principal, final Map<String, String[]> parameters,
			final List<Locale> locales, final HttpServletRequest request ){
		final SubSession subSession = subSessionIdToSubSession.get(subSessionId);
		if( subSession != null && subSession.isDestroyed() != true ){
			return new SessionResult( subSessionId, subSession, false );
		} else if( create ){
			return createSubSession( factory, remoteAddress, principal, parameters, locales, request );
		} else {
			return null;
		}
	}

	public static Session get( final WebSocketSession webSocketSession ){
		final Object sessionObject = webSocketSession.getAttributes().get(ATTRIBUTE_NAME);
		if( sessionObject instanceof Session ){
			return (Session) sessionObject;
		} else {
			return null;
		}
	}

	public static void set( final Session session, final Map<String, Object> attributes ){
		attributes.put(ATTRIBUTE_NAME, session);
	}

	public static Session get( final HttpSession httpSession, final ApplicationContext context,
			final WCardinalConfiguration configuration, final Scheduler scheduler, final boolean create ){
		synchronized(Session.class){
			final String sessionId = httpSession.getId();
			final Session session = sessionIdToSession.get(sessionId);
			if( session != null ) return session;

			if( create ){
				final Session result = new Session( httpSession.getId(), context, configuration, scheduler );
				sessionIdToSession.put(sessionId, result);
				return result;
			} else {
				return null;
			}
		}
	}

	public static Session get( final HttpSession httpSession ){
		return get( httpSession, null, null, null, false );
	}

	/**
	 * Removes the session associated with the specified HTTP session ID.
	 *
	 * @param httpSessionId HTTP session ID of HTTP session associated with the session to be removed
	 * @return removed session or null if not exists
	 */
	@ThreadSafe
	public static Session remove( final String httpSessionId ){
		synchronized(Session.class){
			return sessionIdToSession.remove(httpSessionId);
		}
	}

	/**
	 * Destroys this session.
	 */
	@ThreadSafe
	public void destroy(){
		final Map<String, SubSession> subSessionIdToSubSession;
		synchronized( this ){
			if( destroyed == true ) return;
			destroyed = true;
			subSessionIdToSubSession = this.subSessionIdToSubSession;
			this.subSessionIdToSubSession = null;
		}

		if( subSessionIdToSubSession == null ) return;
		for( final SubSession subSession: subSessionIdToSubSession.values() ){
			subSession.destroy();
			logger.info("{} {} Removed", sessionId, subSession.getId());
		}

		logger.info("{} Destroyed", sessionId);
	}

	/**
	 * Finds destroyed sub sessions and removes them from this session.
	 */
	@ThreadSafe
	public synchronized void cleanup(){
		for( final Iterator<Map.Entry<String, SubSession>> i = subSessionIdToSubSession.entrySet().iterator(); i.hasNext(); ) {
			final SubSession subSession = i.next().getValue();
			if( subSession.isDestroyed() ) {
				i.remove();
				logger.info("{} {} Removed", sessionId, subSession.getId());
			}
		}
	}

	/**
	 * Returns true if this session is destroyed.
	 *
	 * @return true if destroyed
	 */
	@ThreadSafe
	public boolean isDestroyed(){
		synchronized( this ){
			return destroyed;
		}
	}

	@Override
	public void run() {
		if( isDestroyed() != true ){
			cleanup();
			scheduler.schedule(this, Instant.ofEpochMilli(System.currentTimeMillis() + maximumIdleTime));
		}
	}
}
