/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.controller.PageContext;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

public class PageController extends VisibilityController implements PageContext {

	public PageController(final String name, final Controller parent,
			final ControllerFactory factory, final Object instance,
			final ControllerBaggage baggage, final ArrayNode factoryParameters,
			final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks ) {
		super(name, parent, factory, instance, baggage, factoryParameters, lock, tasks);
	}

	@Override
	public void show(){
		for( final Controller parent: parents ){
			parent.show(name);
		}
	}

	@Override
	public void hide(){
		for( final Controller parent: parents ){
			parent.hide(name);
		}
	}

	@Override
	public boolean isShown(){
		for( final Controller parent: parents ){
			return ( parent.isShown(name) && parent.isShown() );
		}
		return false;
	}
}
