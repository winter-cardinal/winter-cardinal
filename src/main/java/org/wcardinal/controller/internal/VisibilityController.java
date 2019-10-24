/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.controller.VisibilityControllerContext;
import org.wcardinal.controller.data.internal.SStringImpl;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

public class VisibilityController extends Controller implements VisibilityControllerContext {
	final static String DISPLAY_NAME_ID = "$dn";
	SStringImpl displayName;

	public VisibilityController(final String name, final Controller parent,
			final ControllerFactory factory, final Object instance,
			final ControllerBaggage baggage, final ArrayNode factoryParameters,
			final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks) {
		super(name, parent, factory, instance, baggage, factoryParameters, lock, tasks);
		visibility.set(false);
	}

	@Override
	void createAdditionals() {
		displayName = new SStringImpl( findDisplayName() );
		displayName.setParent(this);
		displayName.setLock(lock);
		put( DISPLAY_NAME_ID, displayName );
	}

	private String findDisplayName(){
		if( factory.displayName != null ){
			return factory.displayName;
		}

		if( factory.displayNameMessage != null ){
			final String message = factory.displayNameMessage;
			return factory.context.getMessage(message, null, message, getLocale());
		}

		return getName();
	}

	@Override
	public String getDisplayName(){
		return displayName.get();
	}

	@Override
	public void setDisplayName( final String displayName ){
		this.displayName.set( displayName );
	}
}
