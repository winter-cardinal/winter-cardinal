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
import org.springframework.stereotype.Component;

/**
 * Marks an annotated class as a page exposed to browsers.
 * Pages can be fields of controllers, other pages, popups or components.
 * Pages literally represent a page, have visibility states.
 * Among pages belonging to a controller, at most one page can be visible simultaneously.
 * For controlling visibilities, use show/hide methods of
 * {@link org.wcardinal.controller.PageFacade} or {@link org.wcardinal.controller.AbstractPage}.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Scope("prototype")
public @interface Page {

}
