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
 * Marks a method as a exception handler. When exceptions are thrown from methods annotated with annotations listed below,
 * the most appropriate handler is chosen and invoked. In the case that exceptions raised from {@link Task} /
 * {@link Callable} methods are handled by {@link TaskExceptionHandler} / {@link CallableExceptionHandler}
 * methods without raising another exceptions, handlers are not invoked.
 *
 * <pre><code> {@literal @}Controller
 * class MyController extends AbstractController{
 *   {@literal @}Task
 *   void foo(){
 *     throw new RuntimeException();
 *   }
 *
 *   {@literal @}ExceptionHandler( "foo" )
 *   void fooExceptionHandler( RuntimeException e ){
 *     // Called when the task "foo" raises an exception
 *   }
 * }
 * </code></pre>
 *
 * Supported annotations are:
 * <ul>
 * <li>{@link OnCreate}</li>
 * <li>{@link OnPostCreate}</li>
 * <li>{@link OnDestroy}</li>
 * <li>{@link OnChange}</li>
 * <li>{@link OnShow}</li>
 * <li>{@link OnHide}</li>
 * <li>{@link OnTime}</li>
 * <li>{@link OnNotice}</li>
 * <li>{@link Task}</li>
 * <li>{@link Callable}</li>
 * <li>{@link TaskExceptionHandler}</li>
 * <li>{@link CallableExceptionHandler}</li>
 * </ul>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandler {
	/**
	 * Names of methods and fields annotated exception handler handles.
	 * If it is empty, annotated exception handler handles all the methods and fields.
	 *
	 * @return Names of methods and fields annotated exception handler handles.
	 */
	String[] value() default { AbstractTypedMethods.TYPE_ALL };
}
