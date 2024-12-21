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
 * Annotated methods are called when {@link org.wcardinal.controller.annotation.Callable @Callable}
 * methods raise exceptions.
 *
 * <blockquote><pre>
 * &#64;Controller
 * class MyController {
 *   &#64;Callable
 *   void foo(){
 *     throw new RuntimeException();
 *   }
 *
 *   &#64;CallableExceptionHandler
 *   void exceptionHandler(Exception e) {
 *     // Called when callable methods raise exceptions
 *   }
 * }</pre></blockquote>
 *
 * If the {@link #value} is set to other than {@code AbstractTypedMethods.TYPE_ALL},
 * the methods are called only when exceptions are thrown by the specified callable methods.
 *
 * <blockquote><pre>
 * &#64;Controller
 * class MyController {
 *   &#64;Callable
 *   void foo(){
 *     throw new RuntimeException();
 *   }
 *
 *   &#64;Callable
 *   void bar(){
 *     throw new RuntimeException();
 *   }
 *
 *   &#64;CallableExceptionHandler("foo")
 *   void exceptionHandler(Exception e) {
 *     // This method will be called when the callable method "foo" raises exceptions.
 *     // But won't be called when the callable method "bar" raises exceptions.
 *   }
 * }</pre></blockquote>
 *
 * If the thrown exceptions are not assignable to the argument of annotated methods,
 * methods are not called.
 *
 * <blockquote><pre>
 * &#64;Controller
 * class MyController {
 *   &#64;Callable
 *   void foo(){
 *     throw new RuntimeException();
 *   }
 *
 *   &#64;CallableExceptionHandler
 *   void exceptionHandler(IlligalStateException e) {
 *     // This method will not be called since RuntimeException is not assignable to IllegalStateException.
 *   }
 * }</pre></blockquote>
 *
 * If there are more than one excepton handlers, the method with strictest argument type will be called.
 *
 * <blockquote><pre>
 * &#64;Controller
 * class MyController {
 *   &#64;Callable
 *   void foo(){
 *     throw new RuntimeException();
 *   }
 *
 *   &#64;CallableExceptionHandler
 *   void exceptionHandler1(Exception e) {
 *     // This method will not be called since exceptionHandler2 has the more strict argument type.
 *   }
 *
 *   &#64;CallableExceptionHandler
 *   void exceptionHandler2(RuntimeException e) {
 *     // This method will be called since RuntimeException is more strict compared to Exception.
 *   }
 * }</pre></blockquote>
 *
 * The argument can be omitted if thrown exceptions themselve aren't required.
 *
 * <blockquote><pre>
 * &#64;Controller
 * class MyController {
 *   &#64;Callable
 *   void foo(){
 *     throw new RuntimeException();
 *   }
 *
 *   &#64;CallableExceptionHandler
 *   void exceptionHandler1() {}
 * }</pre></blockquote>
 *
 * @see org.wcardinal.controller.annotation.Callable
 */
@Documented
@Decoratable
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CallableExceptionHandler {
	String[] value() default { AbstractTypedMethods.TYPE_ALL };
}
