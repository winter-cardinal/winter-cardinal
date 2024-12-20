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
 * Marks a annotated {@link org.wcardinal.controller.annotation.Callable @Callable} /
 * {@link org.wcardinal.controller.annotation.Task @Task} method as a method invoked
 * via Ajax requests by default.
 *
 * If there are more than one methods of the same name,
 * all methods are called via Ajax requests.
 *
 * Browsers may change this behavior.
 *
 * <pre>{@code&nbsp;
 *    &#64;Controller
 *    class MyController {
 *      &#64;Ajax
 *      &#64;Callable
 *      int callable() {
 *        return 1;
 *      }
 *    }
 * }</pre>
 *
 * @see org.wcardinal.controller.StreamingResult
 * @see org.wcardinal.controller.annotation.Callable
 * @see org.wcardinal.controller.annotation.Task
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ajax {}
