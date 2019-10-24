/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package on_post_create;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.annotation.Ajax;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnPostCreate;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller( protocols={ "unknown" } )
public class OnPostCreateController {
	@Autowired
	ComponentFactory<OnPostCreateFactoryComponent> factory;

	@Autowired
	OnPostCreateComponent component;

	@Autowired
	OnPostCreateShared shared;

	@Autowired @NonNull
	SInteger state;

	@OnCreate
	void init() {
		state.incrementAndGet();
		factory.create( "a" );
	}

	@OnPostCreate
	void postInit() {
		state.incrementAndGet();
		factory.create( "a" );
	}

	@Callable @Ajax
	int getState() {
		return state.get();
	}

	@Callable @Ajax
	int getFactorySize() {
		return factory.size();
	}

	@Callable @Ajax
	int getFactoryState() {
		final OnPostCreateFactoryComponent c = factory.get( 1 );
		return ( c != null ? c.getState() : 0 );
	}
}
