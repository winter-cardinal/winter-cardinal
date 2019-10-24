/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.scope.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

/**
 * Makes a class as to be a controller scope component.
 * The class annotated width this annotation is instantiated for each and every controller.
 * Namely, instances of the class is scoped at the controller level.
 * This scope is typically beneficial for services shared among components belonging to the same controller.
 */
@Qualifier
@Scope("controller")
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention( RetentionPolicy.RUNTIME )
public @interface ControllerScope {

}
