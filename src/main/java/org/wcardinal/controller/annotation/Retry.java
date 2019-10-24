/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.annotation;

/**
 * Represents retry settings.
 */
public @interface Retry {
	/**
	 * Returns retry delay in milliseconds.
	 *
	 * @return retry delay in milliseconds
	 */
	long delay() default 5000;

	/**
	 * Returns timeout limit of retry request in milliseconds.
	 *
	 * @return timeout limit of retry request in milliseconds
	 */
	long timeout() default 5000;

	/**
	 * Returns retry interval in milliseconds.
	 *
	 * @return retry interval in milliseconds
	 */
	long interval() default 15000;
}
