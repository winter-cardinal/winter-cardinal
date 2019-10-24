/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.springframework.context.ApplicationEvent;

class ControllerSessionListenerForSpringSession10xCreated extends ControllerSessionListenerForSpringSession10x {
	ControllerSessionListenerForSpringSession10xCreated(){
		super( "org.springframework.session.events.SessionCreatedEvent" );
	}

	@Override
	public void onApplicationEvent(final ApplicationEvent event) {
		final String sessionId = getSessionId( event );
		if( sessionId != null  ) ControllerSessionListener.sessionCreated( sessionId );
	}
}
