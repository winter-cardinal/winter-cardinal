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

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Marks an annotated class as a controller exposed to browsers.
 *
 * @see org.wcardinal.controller.ControllerFacade
 * @see org.wcardinal.controller.AbstractController
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public @interface Controller {
	/**
	 * Relative URLs to fetch an annotated controller.
	 * If {@link Controller#value()} and {@link Controller#urls()} are omitted,
	 * {@link Controller#name()} in the "Lower-hyphen" format is used as an URL.
	 * For instance, if a class name is MyController, an URL is "/my-controller".
	 *
	 * @return relative URLs to fetch an annotated controller
	 */
	String[] value() default {};

	/**
	 * Relative URLs to fetch an annotated controller.
	 * If {@link Controller#value()} and {@link Controller#urls()} are omitted,
	 * {@link Controller#name()} in the "Lower-hyphen" format is used as an URL.
	 * For instance, if a class name is MyController, an URL is "/my-controller".
	 *
	 * @return relative URLs to fetch an annotated controller
	 */
	String[] urls() default {};

	/**
	 * A name of an annotated controller.
	 * The name must not contains any characters other than alphabets.
	 * The instances of an annotated controller are created using this name at browsers.
	 *
	 * For instance, if the name is "MyController", a class "window.MyController"
	 * and its instance "window.myController" are created.
	 *
	 * By default, the class name of an annotated controller is used as the name.
	 * Therefore, if the class name is MyController, the name is set to the "MyController".
	 *
	 * @return a controller name
	 */
	String name() default "";

	/**
	 * Roles, all of which users accessing an annotated controller must have.
	 *
	 * @return roles users accessing an annotated controller must have
	 */
	String[] roles() default {};

	/**
	 * Keep-alive settings.
	 *
	 * @return keep-alive settings
	 */
	KeepAlive keepAlive() default @KeepAlive();

	/**
	 * Retry settings.
	 *
	 * @return retry settings
	 */
	Retry retry() default @Retry();

	/**
	 * An array of allowed network protocols.
	 * Protocols must be one of the followings:
	 *
	 * <ul>
	 * <li>websocket</li>
	 * <li>polling-x</li>
	 * </ul>
	 *
	 * The {@code x} in the above protocols is a number representing a polling interval in milliseconds.
	 * E.g., {@code "polling-1000"}, {@code "polling-15000"}.
	 *
	 * If the polling interval is not specified, namely if the string {@code "polling"} is given,
	 * the polling interval is set to {@code 100} ms.
	 *
	 * If an array is empty, the default protocols are used.
	 *
	 * @return allowed network protocols
	 */
	String[] protocols() default {};

	/**
	 * An array of the two title separators.
	 * The first separator is used as the separator between the base title specified by a title tag and the rest of the title.
	 * The second separator is used as the separator among the parts of the rest of the title.
	 *
	 * @return title separators
	 */
	String[] separators() default { " - ", " / " };

	/**
	 * An array of the messages of the two title separators.
	 * The first separator is used as the separator between the base title specified by a title tag and the rest of the title.
	 * The second separator is used as the separator among the parts of the rest of the title.
	 *
	 * @return messages of title separators
	 */
	String[] separatorMessages() default {};
}
