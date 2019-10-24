/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import java.util.concurrent.ScheduledFuture;

/**
 * A delayed result-bearing action that can be cancelled.
 *
 * @param <T> result type
 */
public interface TimeoutFuture<T> extends ScheduledFuture<T> {
	/**
	 * Returns the request ID.
	 *
	 * @return request ID
	 */
	long getId();
}
