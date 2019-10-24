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
 * Marks an annotated field or a annotated class as a constant which is visible to browsers.
 * The field values must be available immediately after the locked {@link org.wcardinal.controller.annotation.OnCreate @OnCreate} methods' execution.
 * The field values at that time will be sent to browsers.
 * The field values at that time must be serializable by Jackson.
 * Must not use this annotation on the following fields:
 *
 * <ul>
 * <li>Fields of types in the package org.wcardinal.controller</li>
 * <li>Fields of types in the package org.wcardinal.controller.data</li>
 * <li>Fields of types annotated with {@link org.wcardinal.controller.annotation.Page}</li>
 * <li>Fields of types annotated with {@link org.wcardinal.controller.annotation.Popup}</li>
 * <li>Fields of types annotated with {@link org.wcardinal.controller.annotation.Component}</li>
 * <li>Fields of types annotated with {@link org.wcardinal.controller.annotation.SharedComponent}</li>
 * </ul>
 */
@Documented
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Constant {
	Class<?>[] value() default {};
}
