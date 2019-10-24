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
 * The annotated static methods are invoked with one optional argument HttpServletRequest
 * for checking whether the controller owing them is appropriate for the browser who sent the request.
 * The methods are supposed to return true if and only if the controller is appropriate.
 * The methods are executed in the thread of the corresponding HTTP request.
 * Thread locals set by interceptors or filters, thus, are accessible from the methods.
 * Not necessarily have to check roles the request has since it is guaranteed that the request has all
 * roles specified at {@link org.wcardinal.controller.annotation.Controller#roles()}.
 *
 * <pre><code> {@literal @}OnCheck
 * static boolean onCheck( HttpServletRequest request ){
 *     // Returns true if and only if a controller is appropriate for the browser who sent the request.
 *     return true;
 * }
 * </code></pre>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnCheck {

}
