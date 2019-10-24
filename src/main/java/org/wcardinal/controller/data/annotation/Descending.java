/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets sorting orders of annotated SNavigableMap fields to the descending order.
 */
@Documented
@Target( ElementType.FIELD )
@Retention(RetentionPolicy.RUNTIME)
public @interface Descending {

}
