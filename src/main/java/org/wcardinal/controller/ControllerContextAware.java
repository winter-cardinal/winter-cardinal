/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

/**
 * Interface to be implemented by any classes that wishes to be notified
 * of {@link org.wcardinal.controller.ControllerContext} that it runs in.
 */
public interface ControllerContextAware {
	/**
	 * Sets {@link org.wcardinal.controller.ControllerContext} that this object runs in.
	 *
	 * @param context {@link org.wcardinal.controller.ControllerContext} that this object runs in
	 */
	void setControllerContext( final ControllerContext context );
}
