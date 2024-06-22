/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.controller.internal.ControllerBaggage;
import org.wcardinal.controller.internal.ControllerFactory;
import org.wcardinal.controller.internal.IdleChecker;
import org.wcardinal.controller.internal.RootController;
import org.wcardinal.io.message.MessageReceiver;
import org.wcardinal.io.message.ReceivedRequestMessage;
import org.wcardinal.util.thread.Scheduler;

/**
 * Central class of sub sessions.
 */
public class SubSession implements MessageReceiver, IdleChecker {
	final Logger logger = LoggerFactory.getLogger(SubSession.class);

	final Endpoint endpoint;
	final String sessionId;
	final String subSessionId;
	final String remoteAddress;
	final RootController rootController;
	final Scheduler scheduler;
	final ControllerFactory factory;
	final AtomicBoolean isDestroyed = new AtomicBoolean(false);
	final ControllerBaggage baggage;
	final SubSessionChecker checker;
	final WCardinalConfiguration configuration;

	public SubSession( final String sessionId, final String subSessionId, final WCardinalConfiguration configuration,
			final Scheduler scheduler, final ControllerFactory factory, final String remoteAddress,
			final Principal principal, final Map<String, String[]> parameters, final List<Locale> locales, final HttpServletRequest request ){

		this.sessionId = sessionId;
		this.subSessionId = subSessionId;
		this.remoteAddress = remoteAddress;
		this.scheduler = scheduler;
		this.factory = factory;
		this.configuration = configuration;
		this.endpoint = new Endpoint( sessionId, subSessionId, scheduler, this, this, configuration );
		this.baggage = new ControllerBaggage( sessionId, subSessionId, remoteAddress,
				principal, scheduler, endpoint, parameters, locales, configuration );

		factory.onRequest( request, baggage );

		logger.info("{} {} Created {address:{}}", sessionId, subSessionId, remoteAddress);

		this.rootController = factory.createRoot( baggage );
		this.rootController.create();

		this.checker = SubSessionCheckers.create( this );
		this.checker.start();
	}

	public Endpoint getEndpoint(){
		return endpoint;
	}

	public String getId(){
		return subSessionId;
	}

	public RootController getRootController(){
		return rootController;
	}

	public WCardinalConfiguration getConfiguration() {
		return configuration;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	// Destroy
	public void destroy(){
		if( isDestroyed.getAndSet(true) ) return;

		// Cancel the scheduled future
		checker.stop();

		// SRoot
		rootController.destroy();

		// Endpoint
		endpoint.close();

		// Remove a bean
		factory.destroy( rootController );

		logger.info("{} {} Destroyed", sessionId, subSessionId);
	}

	public boolean isDestroyed(){
		return isDestroyed.get();
	}

	@Override
	public void receive( final ReceivedRequestMessage message ) {
		if( message.getType().equals("a") ){
			logger.debug("{} {} Received [ack     ] request {}", sessionId, subSessionId, message);
			rootController.handleAuthorizeMessage( message, true );
		} else if( message.getType().equals("b") ){
			logger.debug("{} {} Received [accept  ] request {}", sessionId, subSessionId, message);
			rootController.handleConnectAcceptMessage( message, true );
		} else if( message.getType().equals("u") ){
			logger.debug("{} {} Received [update  ] request {}", sessionId, subSessionId, message);
			rootController.handleUpdateMessage( message, true );
		}
	}

	@Override
	public void check() {
		this.checker.check();
	}
}
