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
 * When a method is called, a controller owing the method is locked.
 * Accessing fields of the controller owing the method, thus, is thread safe.

 * <pre>{@code&nbsp;
 *    import org.wcardinal.controller.annotation.Callable;
 *    import org.wcardinal.controller.annotation.Controller;
 *
 *    &#64;Controller
 *    class MyController {
 *      &#64;Callable
 *      int callable() {
 *        return 1;
 *      }
 *    }
 * }</pre>
 *
 * In browsers, {@code MyController#callable()} can be called as follows:
 *
 * <pre>{@code&nbsp;
 *    <script src="my-controller"></script>
 *    <script>
 *       console.log(await myController.callable()); // Prints 1
 *    </script>
 * }</pre>
 *
 * @see org.wcardinal.controller.StreamingResult
 * @see org.wcardinal.controller.annotation.Ajax
 * @see org.wcardinal.controller.annotation.Task
 */
@Documented
@Decoratable
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Callable {

}
