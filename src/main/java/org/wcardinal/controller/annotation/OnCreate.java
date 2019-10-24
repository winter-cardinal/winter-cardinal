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
 * Annotated methods are called when the controllers owing them get ready.
 * At the timing of the Spring's javax.annotation.PostConstruct, {@link org.springframework.beans.factory.InitializingBean#afterPropertiesSet()} or init-method
 * some methods of the controllers may not work properly.
 *
 * Any changes made in {@link Locked} {@link OnCreate} methods are sent as part of HTTP responses for script tags
 * if {@link org.wcardinal.configuration.WCardinalConfiguration#isControllerVariableEmbeddable()} is true,
 * while changes made in {@link Unlocked} {@link OnCreate} methods may or may not be sent depending on an execution timing.
 * Use {@link OnPostCreate} instead to stop sending changes.
 *
 * When a method is called, a controller owing the method is locked.
 * Accessing fields of the controller owing the method, thus, is thread safe.
 *
 * @see org.wcardinal.configuration.WCardinalConfiguration#isControllerVariableEmbeddable()
 * @see org.wcardinal.configuration.WCardinalConfiguration#setControllerVariableEmbeddable(boolean)
 * @see OnPostCreate
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnCreate {

}
