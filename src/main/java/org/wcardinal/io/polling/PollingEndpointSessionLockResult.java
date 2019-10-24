/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.polling;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import org.wcardinal.io.EndpointSessionLockResult;

public class PollingEndpointSessionLockResult implements EndpointSessionLockResult {
	final AsyncContext context;
	final HttpServletResponse response;
	final OutputStream stream;

	PollingEndpointSessionLockResult( AsyncContext context, HttpServletResponse response, OutputStream stream ){
		this.context = context;
		this.response = response;
		this.stream = stream;
	}

	@Override
	public void cancel() {
		try {
			response.sendError(HttpServletResponse.SC_NO_CONTENT);
		} catch( final Exception e ) {

		}
		unlock();
	}

	@Override
	public void unlock() {
		IOUtils.closeQuietly( stream );
		try {
			if( context != null ){
				context.complete();
			}
		} catch( final Exception e ){

		}
	}

	@Override
	public void send( final CharSequence message, final boolean isLast ) throws IOException {
		stream.write( message.toString().getBytes(StandardCharsets.UTF_8) );
	}
}
