/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package on_post_create;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Ajax;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnPostCreate;
import org.wcardinal.controller.annotation.SharedComponent;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.annotation.NonNull;

@SharedComponent
public class OnPostCreateShared {
	@Autowired @NonNull
	SInteger state;

	@OnCreate
	void init() {
		state.incrementAndGet();
	}

	@OnPostCreate
	void postInit() {
		state.incrementAndGet();
	}

	@Callable @Ajax
	int getState() {
		return state.get();
	}
}
