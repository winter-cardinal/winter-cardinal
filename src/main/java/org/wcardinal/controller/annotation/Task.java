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
 * <pre>{@code&nbsp;
 *    import org.wcardinal.controller.annotation.Controller;
 *    import org.wcardinal.controller.annotation.Task;
 *
 *    &#64;Controller
 *    class MyController {
 *      &#64;Task
 *      int task() {
 *        return 1;
 *      }
 *    }
 * }</pre>
 *
 * @see org.wcardinal.controller.StreamingResult
 * @see org.wcardinal.controller.annotation.Ajax
 * @see org.wcardinal.controller.annotation.Callable
 */
@Documented
@Decoratable
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {

}
