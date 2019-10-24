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
 * Makes annotated fields or fields of annotated classes to be soft.
 * Soft fields on servers are cleared or set to null event if they are annotated with {@link org.wcardinal.controller.data.annotation.NonNull}
 * when their values are not required by browsers for saving memory consumptions.
 * Soft fields on browsers are not cleared or set to null.
 */
@Documented
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Soft {

}
