/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.springframework.core.ResolvableType;

import org.wcardinal.controller.data.internal.SMapImpl;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

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
	public void putResultIfExists( final String key, final Object result ){
		if( requests.containsKey( key ) ) {
			results.put( key, result );
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
