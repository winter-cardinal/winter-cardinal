/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.wcardinal.configuration.WCardinalConfigurationSupport;

@Configuration
@ConditionalOnWebApplication
@EnableWebSocket
public class WebSocketConfig {
	@Bean
	public HandlerMapping webSocketConfigHandlerMapping(
		final ApplicationContext context,
		final WCardinalConfigurationSupport configuration
	) {
		final var registory = new WebSocketHandlerRegistory();
		registerWebSocketHandlers(context, configuration, registory);
		if (registory.requiresTaskScheduler()) {
			registory.setTaskScheduler(newScheduler());
		}
		return registory.getHandlerMapping();
	}

	private ThreadPoolTaskScheduler newScheduler() {
		final var scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("wcws-");
		scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
		scheduler.setRemoveOnCancelPolicy(true);
		return scheduler;
	}

	private void registerWebSocketHandlers(
		final ApplicationContext context,
		final WCardinalConfigurationSupport configuration,
		final WebSocketHandlerRegistry registry
	) {
		final Class<? extends WebSocketHandler> endpointClass;
		final HandshakeInterceptor interceptor;
		if (configuration.isSharedConnectionEnabled()) {
			endpointClass = SharedWebSocketEndpoint.class;
			interceptor = new SharedWebSocketIntercepter();
		} else{
			endpointClass = WebSocketEndpoint.class;
			interceptor = new WebSocketIntercepter();
		}

		final var handler = new PerConnectionWebSocketHandler(endpointClass, true);
		handler.setBeanFactory(context.getAutowireCapableBeanFactory());

		final var registration = registry.addHandler(handler, configuration.getWebSocketPath())
			.addInterceptors(interceptor);

		final var allowedOrigins = configuration.getAllowedOrigins();
		if (allowedOrigins != null && 0 < allowedOrigins.length) {
			registration.setAllowedOrigins(allowedOrigins);
		}
	}
}
