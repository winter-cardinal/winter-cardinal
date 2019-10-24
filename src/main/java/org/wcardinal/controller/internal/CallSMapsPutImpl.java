/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.io.OutputStream;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import org.wcardinal.util.json.Json;

public class CallSMapsPutImpl implements CallSMapsPut {
	final AsyncContext context;

	public CallSMapsPutImpl( final HttpServletRequest request ) {
		this.context = toAsyncContext( request, 0 );
	}

	AsyncContext toAsyncContext( final HttpServletRequest request, final long timeout ){
		try {
			final AsyncContext result = request.startAsync();
			result.setTimeout( timeout );
			return result;
		} catch( final Exception e ){
			return null;
		}
	}

	@Override
	public void putResultIfExists( final String key, final Object result ) {
		HttpServletResponse response = null;
		try {
			response = (HttpServletResponse)context.getResponse();
			response.setStatus(HttpServletResponse.SC_OK);
			response.setHeader("Content-Type", "application/json");
			OutputStream oStream = response.getOutputStream();
			try {
				Json.non_closing_writer.writeValue(oStream, result);
			} finally {
				IOUtils.closeQuietly(oStream);
			}
		} catch ( final Exception e1 ) {
			if( response != null ) {
				try {
					response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
				} catch ( final Exception e2 ) {
					// DO NOTHING
				}
			}
		} finally {
			complete();
		}
	}

	void complete(){
		try {
			if( context != null ){
				context.complete();
			}
		} catch( final Exception e ){

		}
	}
}
