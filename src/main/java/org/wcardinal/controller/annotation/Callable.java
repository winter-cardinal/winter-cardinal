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
 * Marks an annotated method as a method which can be called from browsers.
 *
 * <blockquote><pre>
 * import org.wcardinal.controller.annotation.Callable;
 * import org.wcardinal.controller.annotation.Controller;
 *
 * &#64;Controller
 * class MyController {
 *   &#64;Callable
 *   String hello(String name) {
 *     return "Hello, " + name + "!";
 *   }
 * }</pre></blockquote>
 *
 * In browsers, {@code MyController#hello(String)} can be called as follows:
 *
 * <blockquote><pre>{@code
 * <script src="my-controller"></script>
 * <script>
 *    console.log(await myController.hello("Cardinal")); // Prints "Hello, Cardinal!"
 * </script>}</pre></blockquote>
 *
 * <h2>Thread Safety</h2>
 *
 * When a method is called, a controller owing the method is locked.
 * Accessing fields of the controller owing the method, thus, is thread safe.
 * This default behavior can be changed. Please refer to {@link org.wcardinal.controller.annotation.Locked @Unlocked}.
 *
 * <h2>Type Declaration for TypeScript</h2>
 *
 * In the TypeScript projects, the type declaration of {@code MyController} shown in above will look like this.
 *
 * <blockquote><pre>
 * import { controller } from "@wcardinal/wcardinal";
 *
 * interface MyController extends controller.Controller {
 *     hello: controller.Callable&lt;string, [name: string]&gt;;
 * }</pre></blockquote>
 *
 * If methods like `controller.Controller#on(string, function): this` and
 * `controller.Callable#timeout(number)` aren't mandatory, the declaration
 * can be simplified to:
 *
 * <blockquote><pre>
 * interface MyController {
 *     hello(name: string): string;
 * }</pre></blockquote>
 *
 * @see org.wcardinal.controller.StreamingResult
 * @see org.wcardinal.controller.annotation.Ajax
 * @see org.wcardinal.controller.annotation.Task
 * @see org.wcardinal.controller.annotation.Locked
 * @see org.wcardinal.controller.annotation.Unlocked
 */
@Documented
@Decoratable
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Callable {

}
