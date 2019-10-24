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
 * Annotated methods are called immediately after {@link OnCreate} methods.
 * In contrast to {@link OnCreate}, no changes made in {@link OnPostCreate} methods are sent as part of HTTP responses for script tags.
 *
 * When an annotated method is called, a controller who has the method is locked.
 * Accessing fields of the controller, thus, is thread safe.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnPostCreate {

}
