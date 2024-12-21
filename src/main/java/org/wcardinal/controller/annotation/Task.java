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
 * Marks an annotated method as a task.
 *
 * <blockquote><pre>
 * import org.wcardinal.controller.annotation.Controller;
 * import org.wcardinal.controller.annotation.Task;
 *
 * &#64;Controller
 * class MyController {
 *   &#64;Task
 *   String hello(String name) {
 *     return "Hello, " + name + "!";
 *   }
 * }</pre></blockquote>
 *
 * <h2>Thread Safety</h2>
 *
 * Unlike {@link org.wcardinal.controller.annotation.Callable @Callable},
 * when a method is called, a controller owing the method doesn't aquire a lock
 * automatically. This is an intended behavior. So accessing fields of the
 * controller owing the method, thus, might not be thread safe. This default behavior
 * can be changed. Please refer to {@link org.wcardinal.controller.annotation.Locked @Locked}.
 *
 * <h2>Type Declaration for TypeScript</h2>
 *
 * In the TypeScript projects, the type declaration of {@code MyController} shown in above will look like this.
 *
 * <blockquote><pre>
 * import { controller } from "@wcardinal/wcardinal";
 *
 * interface MyController extends controller.Controller {
 *     hello: controller.Task&lt;string, [name: string]&gt;;
 * }</pre></blockquote>
 *
 * @see org.wcardinal.controller.StreamingResult
 * @see org.wcardinal.controller.annotation.Ajax
 * @see org.wcardinal.controller.annotation.Callable
 * @see org.wcardinal.controller.annotation.Locked
 * @see org.wcardinal.controller.annotation.Unlocked
 */
@Documented
@Decoratable
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {

}
