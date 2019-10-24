/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

/**
 * Interface to provide methods for visibility controllers.
 */
public interface VisibilityControllerContext extends ControllerContext {
	/**
	 * Returns the display name.
	 *
	 * @return display name
	 */
	public String getDisplayName();

	/**
	 * Sets the display name to the specified string.
	 *
	 * @param displayName display name
	 */
	public void setDisplayName( final String displayName );
}
