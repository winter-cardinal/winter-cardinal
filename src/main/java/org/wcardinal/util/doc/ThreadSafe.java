/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.doc;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates the annotated class or method is thread-safe.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe {

}
