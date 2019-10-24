/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.controller.ComponentContext;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

public class ComponentController extends Controller implements ComponentContext {
	public ComponentController(final String name, final Controller parent,
			final ControllerFactory factory, final Object instance, final ControllerBaggage baggage,
			final ArrayNode factoryParameters, final AutoCloseableReentrantLock lock, final TaskInternalQueue tasks ) {
		super(name, parent, factory, instance, baggage, factoryParameters, lock, tasks);
	}
}
