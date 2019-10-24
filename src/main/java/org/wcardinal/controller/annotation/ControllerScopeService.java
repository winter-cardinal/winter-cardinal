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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Changes the scope of an annotated bean to the controller scope.
 * The service is not exposed to browsers and is instantiated for each and every controller.
 * The controller scope is, for example, beneficial for services shared among components belonging to the same controller.
 */
@Documented
@Qualifier
@Component
@Scope("controller")
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention( RetentionPolicy.RUNTIME )
public @interface ControllerScopeService {

}
