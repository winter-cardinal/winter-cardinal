/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.polling;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.io.EndpointSessionLockResult;

public class PollingEndpointSession implements org.wcardinal.io.EndpointSession, AsyncListener {
	final static Logger logger = LoggerFactory.getLogger(PollingEndpointSession.class);

	final AtomicBoolean isValid;
	final AsyncContext context;

	public PollingEndpointSession( final HttpServletRequest request, final WCardinalConfiguration configuration ){
		isValid = new AtomicBoolean(true);
		context = toAsyncContext( request, configuration.getPollingTimeout() );
	}

	public AsyncContext toAsyncContext( final HttpServletRequest request, final long timeout ){
		try {
			final AsyncContext result = request.startAsync();
			result.setTimeout( timeout );
			result.addListener(this);
			return result;
		} catch( final Exception e ){
			logger.error("Failed to retrieve a async context", e);
			return null;
		}
	}

	@Override
	public void close() {
		send();
	}

	@Override
	public EndpointSessionLockResult tryLock() {
		final boolean isReady = ( context != null && isValid.compareAndSet( true, false ) );

		if( isReady ) {
			HttpServletResponse response = null;
			try {
				response = (HttpServletResponse)context.getResponse();
				response.setStatus(HttpServletResponse.SC_OK);
				response.setHeader("Cache-Control", "max-age=0");
				response.setHeader("Content-Type", "application/json");
				response.setCharacterEncoding("UTF-8");
				final OutputStream stream = response.getOutputStream();
				return new PollingEndpointSessionLockResult( context, response, stream );
			} catch( final Exception e1 ) {
				if( response != null ) {
					try {
						response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
					} catch ( final Exception e2 ) {
						// DO NOTHING
					}
				}

				complete();

				return null;
			}
		}

		return null;
	}

	void complete(){
		try {
			if( context != null ){
				context.complete();
			}
		} catch( final Exception e ){

		}
	}

	@Override
	public void onComplete(final AsyncEvent event) throws IOException {
		isValid.set( false );
	}

	@Override
	public void onTimeout(final AsyncEvent event) throws IOException {
		send();
	}

	void send(){
		final boolean isValid = this.isValid.getAndSet( false );
		if( isValid != true ) return;

		if( context == null ) return;

		final HttpServletResponse response = (HttpServletResponse) context.getResponse();
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Cache-Control", "max-age=0");
		response.setHeader("Content-Type", "application/json");
		response.setCharacterEncoding("UTF-8");
		complete();
	}

	@Override
	public void onError(final AsyncEvent event) throws IOException {
		isValid.set( false );

		if( context == null ) return;
		complete();
	}

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {

	}
}
