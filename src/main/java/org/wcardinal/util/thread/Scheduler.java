/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.thread;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class Scheduler extends ThreadPoolTaskScheduler {
	private static final long serialVersionUID = -6330347721910781746L;

	public Scheduler( final int poolSize ){
		this( "wcardinal-", poolSize );
	}

	public Scheduler( final String threadNamePrefix, final int poolSize ){
		super();
		this.setPoolSize( poolSize );
		this.setThreadNamePrefix( threadNamePrefix );
		this.setRemoveOnCancelPolicy(true);
		this.initialize();
	}

	public ScheduledFuture<?> schedule( final Runnable runnable, final long delay ) {
		return getScheduledExecutor().schedule( runnable, delay, TimeUnit.MILLISECONDS );
	}
}
