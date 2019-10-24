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
 * Makes methods annotated with a {@link Decoratable} annotation tracked methods.
 *
 * @see Callable
 * @see OnChange
 * @see OnHide
 * @see OnNotice
 * @see OnShow
 * @see OnTime
 * @see org.wcardinal.controller.ControllerContext#isHeadCall()
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tracked {
}
