/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.controller.ControllerIo;
import org.wcardinal.controller.internal.IdleChecker;
import org.wcardinal.io.message.MessageSender;
import org.wcardinal.io.message.MessageFormatException;
import org.wcardinal.io.message.MessageReceiver;
import org.wcardinal.io.message.ReceivedRequestMessages;
import org.wcardinal.io.message.ReceivedRequestMessage;
import org.wcardinal.io.message.RequestMessage;
import org.wcardinal.util.thread.Scheduler;

/**
 * Central class of endpoints.
 */
public class Endpoint implements MessageSender, ControllerIo {
	static final Logger logger = LoggerFactory.getLogger(Endpoint.class);

	final int MESSAGE_POOL_SIZE;
	final long MAXIMUM_IDLE_TIME;
	final int PARTIAL_MESSAGE_SIZE;
	final boolean IS_PARTIAL_MESSAGE_ENABLED;

	long lastAccess = System.currentTimeMillis();
	boolean isFirst = true;
	boolean isValid = true;
	EndpointSession session = null;
	final MessageReceiver handler;
	final IdleChecker checker;
	final Scheduler scheduler;

	final List<RequestMessage> messages = new ArrayList<>();
	final EndpointRequestMessageWriter messageWriter;

	final String sessionId;
	final String subSessionId;

	public Endpoint( final String sessionId, final String subSessionId, final Scheduler scheduler,
			final MessageReceiver handler, final IdleChecker checker, final WCardinalConfiguration configuration ) {
		MESSAGE_POOL_SIZE = configuration.getMessagePoolSize();
		MAXIMUM_IDLE_TIME = configuration.getMaximumIdleTime();
		PARTIAL_MESSAGE_SIZE = configuration.getPartialMessageSize();
		IS_PARTIAL_MESSAGE_ENABLED = configuration.isPartialMessageEnabled();

		this.sessionId = sessionId;
		this.subSessionId = subSessionId;
		this.handler = handler;
		this.checker = checker;
		this.scheduler = scheduler;

		this.messageWriter = new EndpointRequestMessageWriter( this, messages, true );

		logger.debug("{} {} Created", sessionId, subSessionId);
	}

	public boolean set( final EndpointSession session ){
		final EndpointSession oldSession;
		synchronized(this){
			isFirst = false;

			if( touch() ) {
				oldSession = this.session;
				this.session = session;
			} else {
				return false;
			}
		}

		if( oldSession != null ) {
			oldSession.close();
		}

		flush();

		if( oldSession == null ) {
			checker.check();
		}
		return true;
	}

	public boolean unset( final EndpointSession session ) {
		final boolean result;
		synchronized(this) {
			result = (this.session == session);
			if( result ) {
				this.session = null;
			}
		}
		if( result ) {
			checker.check();
		}
		return result;
	}

	public void close(){
		final EndpointSession session;
		synchronized(this){
			if( isValid == false ) return;
			isValid = false;
			session = this.session;
			this.session = null;
		}

		// Session
		if( session != null ) {
			session.close();
		}

		// Message
		clear();

		logger.debug("{} {} Closed", sessionId, subSessionId);
	}

	public void onMessage(final String scMessage){
		if( touch() ) {
			try {
				onMessage( ReceivedRequestMessages.parse(scMessage) );
			} catch ( final MessageFormatException e ) {
				logger.debug("{} {} Malformed message {}", sessionId, subSessionId, scMessage, e);
			}
		}
	}

	public void onMessage(final Reader scMessage){
		if( touch() ) {
			try {
				onMessage( ReceivedRequestMessages.parse(scMessage) );
			} catch ( final MessageFormatException e ) {
				logger.debug("{} {} Malformed message {}", sessionId, subSessionId, scMessage, e);
			}
		}
	}

	void onMessage(final List<ReceivedRequestMessage> messages){
		for( final ReceivedRequestMessage message: messages ){
			if( message == null ) continue;
			scheduler.execute(new EndpointOnMessageRunner( this, message ));
		}
	}

	void onMessage( final ReceivedRequestMessage message ){
		try{
			logger.debug("{} {} Received {}", sessionId, subSessionId, message );
			handler.receive( message );
		} catch (Exception e) {
			logger.error( "Failed to process a received message", e );
		}
	}

	synchronized void add( final RequestMessage message ){
		if( MESSAGE_POOL_SIZE <= messages.size() ){
			messages.remove( 0 );
		}
		messages.add(message);
	}

	synchronized void clear(){
		messages.clear();
	}

	@Override
	public void send( final RequestMessage message ) {
		logger.debug("{} {} Sent {}", sessionId, subSessionId, message);
		add( message );
		flush();
	}

	public void flush() {
		while( send( messages, messageWriter ) ) {
			/* DO NOTHING */
		}
	}

	private boolean send( final Collection<RequestMessage> messages, final EndpointRequestMessageWriter writer ) {
		final EndpointSession session;
		synchronized(this){
			session = this.session;
			if( isValid != true || messages.isEmpty() || session == null ) return false;
		}

		final EndpointSessionLockResult lockResult = session.tryLock();
		if( lockResult != null ) {
			try {
				return send( lockResult, messages, writer );
			} finally {
				lockResult.unlock();
			}
		}

		return false;
	}

	private boolean send( final EndpointSessionLockResult lockResult, final Collection<RequestMessage> messages, final EndpointRequestMessageWriter writer ) {
		return writer.write( PARTIAL_MESSAGE_SIZE, ( IS_PARTIAL_MESSAGE_ENABLED ?
			new EndpointSessionSenderPartial( lockResult ) :
			new EndpointSessionSenderWhole( lockResult )
		));
	}

	/**
	 * Returns true if successfully the last access time is updated.
	 *
	 * @return true if succeeded
	 */
	@Override
	public synchronized boolean touch(){
		if( isValid ) {
			lastAccess = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	@Override
	public synchronized long getIdleTime() {
		return System.currentTimeMillis() - lastAccess;
	}

	@Override
	public synchronized long getLastAccessedTime() {
		return lastAccess;
	}

	@Override
	public long getMaximumIdleTime() {
		return MAXIMUM_IDLE_TIME;
	}

	@Override
	public synchronized boolean hasConnection() {
		return isValid && (this.session != null);
	}

	@Override
	public synchronized boolean hadConnection() {
		return ! isFirst;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public String getSubSessionId() {
		return subSessionId;
	}
}
