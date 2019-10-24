/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.scope.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import org.wcardinal.controller.internal.SharedComponentController;

public class SharedComponentScope implements Scope {
	private final Map<String, Object> components = new HashMap<>();
	private final Map<String, Runnable> callbacks = new HashMap<>();

	@Override
	public synchronized Object get(final String name, final ObjectFactory<?> objectFactory) {
		final Object component = components.get( name );
		if (component != null) return component;

		final Object newComponent = objectFactory.getObject();
		components.put(name, newComponent);
		return newComponent;
	}

	@Override
	public Object remove(final String name) {
		final Object component;
		synchronized( this ) {
			component = components.remove( name );
			callbacks.remove( name );
		}
		if( component != null ) {
			SharedComponentController.destroy( component );
		}
		return component;
	}

	public void destroy() {
		final List<Runnable> callbacks;
		final List<Object> components;
		synchronized( this ) {
			components = new ArrayList<>( this.components.values() );
			callbacks = new ArrayList<>( this.callbacks.values() );
			this.components.clear();
			this.callbacks.clear();
		}
		for( final Object component: components ) {
			SharedComponentController.destroy( component );
		}
		for ( final Runnable callback : callbacks ) {
			callback.run();
		}
	}

	@Override
	public synchronized void registerDestructionCallback( final String name, final Runnable callback ) {
		callbacks.put( name, callback );
	}

	@Override
	public Object resolveContextualObject(final String key) {
		return null;
	}

	@Override
	public String getConversationId() {
		return null;
	}
}
