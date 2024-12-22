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
 * The annotated static methods are invoked with two optional arguments
 * HttpServletRequest and {@link org.wcardinal.controller.ControllerAttributes} when the controller owing them are requested.
 * The methods are executed in the thread of the corresponding HTTP request.
 * Thread locals set by interceptors or filters, thus, are accessible from the methods.
 *
 * <pre><code> {@literal @}OnRequest
 * static void onRequest( HttpServletRequest request, ControllerAttributes attributes ){
 *    // Update controller attributes here
 * }
 * </code></pre>
 *
 * The arguments are optional. All of the following are also, thus, valid.
 *
 * <pre><code> {@literal @}OnRequest
 * static void onRequest(){}
 *
 * {@literal @}OnRequest
 * static void onRequest( HttpServletRequest request ){}
 *
 * {@literal @}OnRequest
 * static void onRequest( ControllerAttributes attributes ){}
 * </code></pre>
 *
 * Values set to the second argument {@code attributes} are accessible by {@link org.wcardinal.controller.ControllerContext#getAttributes()} as follows:
 *
 * <pre><code> {@literal @}Controller
 * class MyController extends AbstractController {
 *     {@literal @}OnRequest
 *     static void onRequest( HttpServletRequest request, ControllerAttributes attributes ){
 *         attributes.put( "name", "Cardinal" );
 *     }
 *
 *     void method(){
 *         System.out.println( getAttributes().get( "name" ) ); // prints "Cardinal"
 *     }
 * }
 * </code></pre>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnRequest {

}
