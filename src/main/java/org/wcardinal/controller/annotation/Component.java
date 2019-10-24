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

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Marks an annotated class as a component exposed to browsers.
 * Components can be fields of controllers, pages, popups or other components.
 * Usually, components are for structurizing or for reusing codes.
 *
 * For additional methods including a method returning a user's remote address,
 * use {@link org.wcardinal.controller.ComponentFacade} or {@link org.wcardinal.controller.AbstractComponent}.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public @interface Component {

}
