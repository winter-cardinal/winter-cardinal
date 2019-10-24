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
 * Marks an annotated page as a default page.
 *
 * <pre><code> {@literal @}Page
 * class MyPage {}
 *
 * {@literal @}Controller
 * class MyController {
 *   // First page 'alice' is not shown at first.
 *   {@literal @}Autowired
 *   MyPage alice;
 *
 *   // Second page named 'bob' is not shown at first.
 *   {@literal @}Autowired
 *   MyPage bob;
 *
 *   // Third page named 'charlie' is shown firstly.
 *   {@literal @}Autowired
 *   {@literal @}Primary
 *   MyPage charlie;
 * }
 * </code></pre>
 */
@Documented
@Target(value={ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Primary {

}
