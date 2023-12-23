/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets the default timeout of annotated callable methods in milliseconds.
 * If the methods do not respond during this timeout interval,
 * browsers treat the methods unresponsive.
 *
 * If there are more than one methods of the same name,
 * the maximum value of timeout values is used.
 *
 * Browsers may override this timeout limit.
 *
 * By default, the timeout limit is {@code 5000} milliseconds.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {
	/**
	 * Returns default timeout limit in milliseconds.
	 *
	 * @return default timeout limit in milliseconds
	 */
	long value() default 5000;

	/**
	 * Returns default timeout limit in milliseconds.
	 * This accepts the same expressions as {@link org.springframework.beans.factory.annotation.Value} &mdash; for example, <code>${app.timeout}</code>.
	 *
	 * @return default timeout limit in milliseconds
	 */
	String string() default "";
}
