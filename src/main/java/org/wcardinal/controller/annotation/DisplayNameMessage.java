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
 * Represents display name settings.
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DisplayNameMessage {
	/**
	 * Returns the message ID of the display name.
	 * If the returned display name is empty, {@link org.wcardinal.controller.ControllerContext#getName()} is used instead.
	 *
	 * @return display name
	 */
	String value() default "";
}
