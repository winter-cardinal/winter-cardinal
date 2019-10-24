/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class ControllerScope implements Scope {
	@Override
	public Object get(final String name, final ObjectFactory<?> objectFactory) {
		final ControllerScopeAttributes attributes = ControllerScopeAttributesHolder.get();
		final Object object = attributes.get( name );
		if (object != null) return object;

		final Object newObject = objectFactory.getObject();
		attributes.set(name, newObject);
		return newObject;
	}

	@Override
	public Object remove(final String name) {
		final ControllerScopeAttributes attributes = ControllerScopeAttributesHolder.get();
		return attributes.remove(name);
	}

	@Override
	public void registerDestructionCallback(final String name, final Runnable callback) {
		final ControllerScopeAttributes attributes = ControllerScopeAttributesHolder.get();
		attributes.registerDestructionCallback(name, callback);
	}

	@Override
	public Object resolveContextualObject(final String key) {
		return null;
	}

	@Override
	public String getConversationId() {
		final ControllerScopeAttributes attributes = ControllerScopeAttributesHolder.get();
		return attributes.getId();
	}
}
