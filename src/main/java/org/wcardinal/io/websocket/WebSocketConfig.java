/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import org.wcardinal.configuration.WCardinalConfigurationSupport;

@Configuration
@Import(WCardinalConfigurationSupport.class)
@ConditionalOnWebApplication
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	@Autowired
	WCardinalConfigurationSupport configuration;

	@Autowired
	ApplicationContext context;

	@Override
	public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
		final Class<? extends WebSocketHandler> endpointClass;
		final HandshakeInterceptor interceptor;
		if( configuration.isSharedConnectionEnabled() ){
			endpointClass = SharedWebSocketEndpoint.class;
			interceptor = new SharedWebSocketIntercepter();
		} else{
			endpointClass = WebSocketEndpoint.class;
			interceptor = new WebSocketIntercepter();
		}

		final PerConnectionWebSocketHandler handler = new PerConnectionWebSocketHandler(endpointClass, true);
		handler.setBeanFactory(context.getAutowireCapableBeanFactory());

		final WebSocketHandlerRegistration registration = registry.addHandler(handler, configuration.getWebSocketPath())
			.addInterceptors(interceptor);

		final String[] allowedOrigins = configuration.getAllowedOrigins();
		if( allowedOrigins != null && 0 < allowedOrigins.length ) registration.setAllowedOrigins( allowedOrigins );
	}
}
