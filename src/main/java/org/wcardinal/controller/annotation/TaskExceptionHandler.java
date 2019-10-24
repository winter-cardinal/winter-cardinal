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
 * Annotated methods are called when the specified tasks raise exceptions.
 *
 * <pre><code> {@literal @}Controller
 * class MyController extends AbstractController{
 *   {@literal @}Task
 *   void foo(){
 *     throw new RuntimeException();
 *   }
 *
 *   {@literal @}TaskExceptionHandler( "foo" )
 *   void fooExceptionHandler( RuntimeException e ){
 *     // Called when the task "foo" raises an exception
 *   }
 * }
 * </code></pre>
 */
@Documented
@Decoratable
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskExceptionHandler {
	String[] value() default { AbstractTypedMethods.TYPE_ALL };
}
