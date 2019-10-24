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
 * Make a field/type to be a read-only filed/type.
 * Namely, a field/type annotated with this annotation is unmodifiable by browsers.
 * Still, a server can change the field/type value.
 *
 * <pre><code> {@literal @}Controller
 * class MyController {
 *     // Field 'name' is unmodifiable by browsers.
 *     {@literal @}Autowired
 *     {@literal @}ReadOnly
 *     SList{@literal <String>} name;
 * }
 * </code></pre>
 */
@Documented
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadOnly {

}
