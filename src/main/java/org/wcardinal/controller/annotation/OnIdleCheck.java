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
 * The annotated methods are invoked with one optional argument `ControllerIoState` to check the idleness of controllers.
 * The lowest value of the longs returned by the methods is used as a delay in milliseconds after which the methods are invoked next time.
 * If the lowest value is less than zero, the corresponding controllers are scheduled to be destroyed.
 *
 * <pre><code> {@literal @}OnIdleCheck
 * static long onIdleCheck( ControllerIo io ){
 *    if( io.getIdleTime() {@literal <} 10000 ) {
 *       return 1000;   // Check 1 second later
 *    } else {
 *       return -1;     // Destroy this controller
 *    }
 * }
 * </code></pre>
 *
 * @see org.wcardinal.controller.ControllerIo
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnIdleCheck {

}
