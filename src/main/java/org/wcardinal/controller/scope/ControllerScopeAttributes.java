/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerScopeAttributes {
	final Logger logger = LoggerFactory.getLogger(ControllerScopeAttributes.class);

	private final Map<String, Object> attributes = new HashMap<>();
	private final Map<String, Runnable> callbacks = new HashMap<>();
	private final String id;

	public ControllerScopeAttributes( final String sessionId, final String subSessionId ){
		this.id = sessionId + "-" + subSessionId;
	}

	public synchronized Object get(final String name) {
		return attributes.get(name);
	}

	public synchronized Object set(final String name, final Object value) {
		return attributes.put(name, value);
	}

	public synchronized Object remove(final String name) {
		final Object result = attributes.remove(name);
		callbacks.remove( name );
		return result;
	}

	public String getId(){
		return this.id;
	}

	public synchronized Runnable registerDestructionCallback(final String name, final Runnable callback) {
		return callbacks.put( name, callback );
	}

	public synchronized void destroy() {
		final List<Runnable> callbacks;
		synchronized( this ) {
			callbacks = new ArrayList<>( this.callbacks.values() );
			this.attributes.clear();
			this.callbacks.clear();
		}
		for ( final Runnable callback : callbacks ) {
			callback.run();
		}
	}

}
