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
 * Makes methods annotated with a {@link Decoratable} annotation debounced methods.
 * Debounced methods are delayed to be invoked until after {@link #interval()} milliseconds have elapsed
 * since the last time the debounced method were invoked.
 * A debounced method is invoked with the last arguments provided to it.
 * Subsequent calls to a debounced method return the result of the last method invocation.
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
public @interface Debounced {
	/**
	 * Returns number of milliseconds to delay.
	 *
	 * @return number of milliseconds to delay
	 */
	int interval();

	/**
	 * Returns true to invoke at the leading edge of the timeout.
	 *
	 * @return true to invoke at the leading edge of the timeout
	 */
	boolean leading() default false;

	/**
	 * Returns true to invoke at the trailing edge of the timeout.
	 *
	 * @return true to invoke at the trailing edge of the timeout
	 */
	boolean trailing() default true;

	/**
	 * Returns maximum time a method is allowed to be delayed before it's invoked.
	 *
	 * @return maximum time a method is allowed to be delayed before it's invoked
	 */
	int maxInterval() default -1;
}
