/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.controller.PopupContext;
import org.wcardinal.controller.data.internal.SBooleanImpl;
import org.wcardinal.controller.data.internal.SChange;
import org.wcardinal.controller.data.internal.SParent;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

public class PopupController extends VisibilityController implements PopupContext {
	final static String IS_SHOWN_ID = "$is";
	protected final SBooleanImpl isShown;

	public PopupController(final String name, final Controller parent,
			final ControllerFactory factory, final Object instance, final ControllerBaggage baggage,
			final ArrayNode factoryParameters, final AutoCloseableReentrantLock lock,
			final TaskInternalQueue tasks, final boolean isShown) {
		super(name, parent, factory, instance, baggage, factoryParameters, lock, tasks);

		this.isShown = new SBooleanImpl( isShown );
		this.isShown.setParent(this);
		this.isShown.setLock(lock);
		put( parent, IS_SHOWN_ID, this.isShown );
		put( IS_SHOWN_ID, this.isShown );
	}

	@Override
	public void onChange( final SParent origin, final SParent parent, final String name, final SChange schange ){
		switch( name ) {
		case IS_SHOWN_ID:
			checkVisibility();
			break;
		default:
			super.onChange(origin, parent, name, schange);
			break;
		}
	}

	@Override
	public void show(){
		isShown.set(true);
	}

	@Override
	public void hide(){
		isShown.set(false);
	}

	@Override
	public boolean isShown(){
		for( final Controller parent: parents ){
			return ( isShown.get() && parent.isShown() );
		}
		return false;
	}
}
