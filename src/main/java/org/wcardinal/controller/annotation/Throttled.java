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
 * Makes methods annotated with a {@link Decoratable} annotation throttled methods.
 * Throttled methods are invoked at most once per every {@link #interval()} milliseconds.
 * A throttled method is invoked with the last arguments provided to it.
 * Subsequent calls to a throttled method return the result of the last method invocation.
 *
 * @see Callable
 * @see OnChange
 * @see OnHide
 * @see OnNotice
 * @see OnShow
 * @see OnTime
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Throttled {
	/**
	 * Returns number of milliseconds to throttle invocations to.
	 *
	 * @return number of milliseconds to throttle invocations to
	 */
	int interval();

	/**
	 * Returns true to invoke at the leading edge of the timeout.
	 *
	 * @return true to invoke at the leading edge of the timeout
	 */
	boolean leading() default true;

	/**
	 * Returns true to invoke at the trailing edge of the timeout.
	 *
	 * @return true to invoke at the trailing edge of the timeout
	 */
	boolean trailing() default true;
}
