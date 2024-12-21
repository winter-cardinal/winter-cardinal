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
 * Sets the default timeout of annotated {@link org.wcardinal.controller.annotation.Callable @Callable}
 * methods in milliseconds. If the methods don't respond during this interval,
 * browsers treat the methods unresponsive. Please note that browsers may override this timeout value.
 * By default, the timeout is set to 5000 milliseconds.
 *
 * If there are more than one methods of the same name,
 * the maximum value of timeout values is used.
 *
 * @see org.wcardinal.controller.annotation.Callable
 * */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {
	/**
	 * The default timeout in milliseconds.
	 *
	 * @return the default timeout in milliseconds
	 */
	long value() default 5000;

	/**
	 * The default timeout expression.
	 * This accepts the same expressions as {@link org.springframework.beans.factory.annotation.Value}
	 * &mdash; for example, <code>${app.timeout}</code>.
	 * The evaluated value is expected to be a number, i.e., the timeout in milliseconds.
	 *
	 * @return the default timeout in milliseconds
	 * @see org.springframework.beans.factory.annotation.Value
	 */
	String string() default "";
}
