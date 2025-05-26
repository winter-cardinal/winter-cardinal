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
 * via Ajax POST requests by default. If there are more than one methods of the same name,
 * all methods are called via Ajax requests.
 *
 * <blockquote><pre>
 * import org.wcardinal.controller.annotation.Callable;
 * import org.wcardinal.controller.annotation.Controller;
 *
 * &#64;Controller
 * class MyController {
 *   &#64;Ajax
 *   &#64;Callable
 *   int callable() {
 *     return 1;
 *   }
 * }</pre></blockquote>
 *
 * Please note that this behavior can be overriden by JavaScript.
 * For instance, the following JavaScript calls {@code MyController#callable()}
 * via WebSocket.
 *
 * <blockquote><pre>{@code
 * <script src="my-controller"></script>
 * <script>
 *    console.log(await myController.callable.unajax().call()); // Prints 1
 * </script>}</pre></blockquote>
 *
 * @see org.wcardinal.controller.StreamingResult
 * @see org.wcardinal.controller.annotation.Callable
 * @see org.wcardinal.controller.annotation.Task
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ajax {}
