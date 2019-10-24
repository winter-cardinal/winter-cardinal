/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.annotation;

/**
 * Represents keep-alive settings
 */
public @interface KeepAlive {
	/**
	 * A timeout limit of keep-alive request in milliseconds.
	 *
	 * @return timeout limit of keep-alive request in milliseconds
	 */
	long timeout() default 5000;

	/**
	 * A session keep-alive interval in milliseconds.
	 * If the interval is negative, set to the half of {@code WCardinalConfiguration#getMaximumIdleTime()}.
	 *
	 * @return keep-alive interval in milliseconds
	 * @see org.wcardinal.configuration.WCardinalConfiguration#getMaximumIdleTime()
	 */
	long interval() default 240000;

	/**
	 * A ping interval in milliseconds.
	 * If the interval is negative, set to the half of {@code WCardinalConfiguration#getMaximumIdleTime()}.
	 *
	 * @return ping interval in milliseconds
	 * @see org.wcardinal.configuration.WCardinalConfiguration#getMaximumIdleTime()
	 */
	long ping() default -1;
}
