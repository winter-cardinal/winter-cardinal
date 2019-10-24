/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

public class ComponentFactoryController extends FactoryController<ComponentController> {

	public ComponentFactoryController(final String name, final Controller parent,
			final ControllerFactory factory, final Object instance,
			final ControllerBaggage baggage,
			final AutoCloseableReentrantLock lock,
			final TaskInternalQueue tasks,
			final ControllerFactory genericTypeFactory) {
		super(name, parent, factory, instance, baggage, lock, tasks, genericTypeFactory);
	}

	@Override
	public ComponentController createDynamic( final String id, final ArrayNode args ){
		try( final Unlocker unlocker = lock() ) {
			final ComponentController controller = childFactory.createDynamicComponent(id, this, baggage, args, lock, tasks);
			if( controller != null ) {
				controllerNameToController.put(id, controller);
				isChanged = true;
			}
			return controller;
		}
	}

	@Override
	public ComponentController destroyDynamic( final String id ){
		try( final Unlocker unlocker = lock() ) {
			final Controller controller = controllerNameToController.remove( id );
			if( controller != null ) {
				controller.onDestroy();
				controller.destroy( this );
				factory.destroy( controller );
			}
			return (ComponentController) controller;
		}
	}

	@Override
	public ComponentController getDynamic(final String name) {
		return (ComponentController) controllerNameToController.get( name );
	}
}
