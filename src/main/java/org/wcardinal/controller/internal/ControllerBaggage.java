/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.controller.ControllerAttributes;
import org.wcardinal.controller.scope.ControllerScopeAttributes;
import org.wcardinal.io.message.MessageSender;
import org.wcardinal.util.thread.Scheduler;

public class ControllerBaggage {
	final String sessionId;
	final String subSessionId;
	final String remoteAddress;
	final Principal principal;
	final Scheduler scheduler;
	final MessageSender messageSender;
	final Map<String, String[]> parameters;
	final ControllerAttributes attributes;
	final List<Locale> locales;
	final ControllerScopeAttributes scopeAttributes;
	final WCardinalConfiguration configuration;

	public ControllerBaggage( final String sessionId, final String subSessionId,
			final String remoteAddress, final Principal principal, final Scheduler scheduler,
			final MessageSender messageSender, final Map<String, String[]> parameters, final List<Locale> locales,
			final WCardinalConfiguration configuration ){
		this.sessionId = sessionId;
		this.subSessionId = subSessionId;
		this.remoteAddress = remoteAddress;
		this.principal = principal;
		this.scheduler = scheduler;
		this.messageSender = messageSender;
		this.parameters = new HashMap<String, String[]>( parameters );
		this.attributes = new ControllerAttributes();
		this.locales = locales;
		this.scopeAttributes = new ControllerScopeAttributes( sessionId, subSessionId );
		this.configuration = configuration;
	}
}
