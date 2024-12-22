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
 * Marks an annotated method as a method invoked by {@link org.wcardinal.controller.ControllerContext#execute(String, Object...) execute(String, Object...)},
 * {@link org.wcardinal.controller.ControllerContext#timeout(String, long, Object...) timeout(String, long, Obejct...)},
 * {@link org.wcardinal.controller.ControllerContext#interval(String, long) interval(String, long)} or
 * {@link org.wcardinal.controller.ControllerContext#interval(String, long, long, Object...) interval(String, long long, Object...)}.
 * The method must be a thread safe.
 *
 * <pre><code> {@literal @}Controller
 * class MyController extends AbstractController{
 *   {@literal @}OnCreate
 *   void init(){
 *     timeout( "foo", 1000 );
 *   }
 *
 *   {@literal @}OnTime( "foo" )
 *   void foo(){
 *     // Called from the init method
 *   }
 *
 *   {@literal @}OnTime( "foo" )
 *   void bar(){
 *     // Called from the init method
 *   }
 * }
 * </code></pre>
 *
 * When the {@link #value()} is omitted, method names are used to identify which {@link org.wcardinal.controller.annotation.OnTime @OnTime} methods should be invoked.
 *
 * <pre><code> {@literal @}Controller
 * class MyController extends AbstractController{
 *   {@literal @}OnCreate
 *   void init(){
 *     timeout( "foo", 1000 );
 *   }
 *
 *   {@literal @}OnTime
 *   void foo(){
 *     // Called from the init method
 *   }
 *
 *   {@literal @}OnTime
 *   void bar(){
 *     // Not called from the init method
 *   }
 * </code></pre>
 *
 * If the {@link #value()} contains {@code "*"}, annotated methods are invoked regardless of a name.
 *
 * <pre><code> {@literal @}Controller
 * class MyController extends AbstractController{
 *   {@literal @}OnCreate
 *   void init(){
 *     timeout( "foo", 1000 );
 *   }
 *
 *   {@literal @}OnTime( "*" )
 *   void bar(){
 *     // Called from the init method
 *   }
 * </code></pre>
 *
 * When a method is called, a controller owing the method is not locked.
 * Accessing fields of the controller owing the method, thus, is not thread safe.
 *
 * <pre><code> {@literal @}Controller
 * class MyController {
 *   {@literal @}Autowired
 *   SString name;
 *
 *   int score;
 *
 *   {@literal @}OnTime
 *   void foo(){
 *     // Not thread safe
 *     score = 12;
 *
 *     // Thread safe
 *     lock();
 *     try{
 *       score = 12;
 *     } finally {
 *       unlock();
 *     }
 *
 *     // Thread safe because {@link org.wcardinal.controller.data.SScalar#set(Object) set(Object)} locks the `MyController` internally.
 *     name.set( "Cardinal" );
 *   }
 * }
 * </code></pre>
 *
 * @see org.wcardinal.controller.ControllerContext#execute(String, Object...)
 * @see org.wcardinal.controller.ControllerContext#timeout(String, long, Object...)
 * @see org.wcardinal.controller.ControllerContext#interval(String, long)
 * @see org.wcardinal.controller.ControllerContext#interval(String, long, long, Object...)
 */
@Documented
@Decoratable
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnTime {
	String[] value() default {};
}
