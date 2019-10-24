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

import org.springframework.context.annotation.Scope;

/**
 * Marks an annotated class as a shared component exposed to browsers.
 * Shared components can be fields of controllers, pages, popups or other components.
 * Usually, shared components are for structurizing or for reusing codes.
 * Shared components are singletons.
 *
 * For additional methods including {@link org.wcardinal.controller.ControllerContext#timeout(String, long, Object...)},
 * use {@link org.wcardinal.controller.ComponentFacade} or {@link org.wcardinal.controller.AbstractComponent}.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@org.springframework.stereotype.Component
@Scope("shared-component")
public @interface SharedComponent {

}
