/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.io.StringWriter;
import java.util.Collections;

import org.springframework.core.ResolvableType;
import org.wcardinal.controller.StreamingResult;
import org.wcardinal.controller.data.internal.SMapImpl;
import org.wcardinal.util.json.Json;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

import com.fasterxml.jackson.core.JsonGenerator;

public class CallSMapsImpl implements CallSMaps {
	private SMapImpl<CallRequest> requests;
	private SMapImpl<Object> results;

	public CallSMapsImpl( final Controller origin, final Controller parent, final AutoCloseableReentrantLock lock, final String requestsId, final String resultsId ) {
		requests = new SMapImpl<CallRequest>();
		requests.init(requestsId, parent, lock, ResolvableType.forClass(CallRequest.class), Properties.empty() );
		requests.addOrigin( origin );

		results = new SMapImpl<Object>();
		results.init(resultsId, parent, lock, ResolvableType.forClass(Object.class), Properties.empty() );
		results.addOrigin( origin );
	}

	@Override
	public void removeOrigin( final Controller origin ) {
		requests.removeOrigin( origin );
		results.removeOrigin( origin );
	}

	@Override
	public void putVoidIfExists( final String key ){
		if( requests.containsKey( key ) ) {
			results.put( key, Collections.emptyList() );
		}
	}

	@Override
	public void putErrorIfExists( final String key, final String error ){
		if( requests.containsKey( key ) ) {
			results.put( key, error );
		}
	}

	@Override
	public void putResultIfExists( final String key, final Object result ){
		if( requests.containsKey( key ) ) {
			results.put( key, toResult(result) );
		}
	}

	private Object toResult(final Object result) {
		if (result instanceof StreamingResult) {
			StringWriter stringWriter = new StringWriter();
			try {
				JsonGenerator generator = Json.mapper.getFactory().createGenerator(stringWriter);
				try {
					generator.writeStartArray();
					((StreamingResult) result).serialize(generator);
					generator.writeEndArray();
				} catch (Exception e) {
					return CallResultType.EXCEPTION;
				} finally {
					generator.close();
				}
				return new StreamedResult(stringWriter.toString());
			} catch (Exception e) {
				return CallResultType.EXCEPTION;
			} finally {
				try {
					stringWriter.close();
				} catch (Exception e) {
					//
				}
			}
		} else {
			return Collections.singleton(result);
		}
	}

	@Override
	public void removeResult( final String key ) {
		results.remove( key );
	}

	@Override
	public void removeResultIfNotExits( final String key ) {
		if( requests.containsKey( key ) != true ) {
			results.remove( key );
		}
	}

	@Override
	public boolean containsResultKey( final String key ) {
		return results.containsKey( key );
	}
}
