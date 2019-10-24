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

import org.wcardinal.util.reflection.AbstractTypedMethods;

/**
 * Marks an annotated method as a method invoked by {@link org.wcardinal.controller.ControllerContext#notify(String, Object...)}
 * or {@link org.wcardinal.controller.ControllerContext#notifyAsync(String, Object...)}.
 *
 * When a method is called, a controller owing the method is locked.
 * Accessing fields of the controller owing the method, thus, is thread safe.
 */
@Documented
@Decoratable
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnNotice {
	String[] value() default { AbstractTypedMethods.TYPE_ALL };
}
