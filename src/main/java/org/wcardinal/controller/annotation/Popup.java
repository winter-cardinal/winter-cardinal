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
 * Marks an annotated class as a popup exposed to browsers.
 * Popups can be fields of controllers, pages、other popups or components.
 * Popups literally represent a popup, have visibility states.
 * For controlling visibilities, use show/hide methods of
 * {@link org.wcardinal.controller.PageFacade} or {@link org.wcardinal.controller.AbstractPopup}.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Scope("prototype")
public @interface Popup {

}
