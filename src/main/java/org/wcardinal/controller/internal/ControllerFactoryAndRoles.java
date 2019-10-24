/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.Map;

class ControllerFactoryAndRoles {
	public ControllerFactory factory;
	public String[] roles;
	public Map<String, Object> keepAlive;
	public Map<String, Object> retry;
	public Map<String, Object> protocols;
	public ControllerTitle title;

	public ControllerFactoryAndRoles( final ControllerFactory factory, final String[] roles,
			final Map<String, Object> keepAlive, final Map<String, Object> retry, final Map<String, Object> protocols,
			final ControllerTitle title ){
		this.factory = factory;
		this.roles = roles;
		this.keepAlive = keepAlive;
		this.retry = retry;
		this.protocols = protocols;
		this.title = title;
	}
}
