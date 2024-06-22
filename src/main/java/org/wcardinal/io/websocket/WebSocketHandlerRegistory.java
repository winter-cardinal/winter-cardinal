/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.websocket;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.socket.config.annotation.ServletWebSocketHandlerRegistry;

/**
 * @since 2.0.0
 */
public class WebSocketHandlerRegistory extends ServletWebSocketHandlerRegistry {
	@Override
	public void setTaskScheduler(TaskScheduler scheduler) {
		super.setTaskScheduler(scheduler);
	}

	@Override
	public boolean requiresTaskScheduler() {
		return super.requiresTaskScheduler();
	}

	@Override
	public AbstractHandlerMapping getHandlerMapping() {
		final var result = super.getHandlerMapping();
		result.setPatternParser(null);
		return result;
	}
}
