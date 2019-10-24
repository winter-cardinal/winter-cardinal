/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.controller.internal.ControllerServletLoader;
import org.wcardinal.controller.internal.ControllerServlet;

/**
 * Utility class for controllers
 */
public class Controllers {
	static final Logger logger = LoggerFactory.getLogger(ControllerServlet.class);

	/**
	 * Returns a controller script and sub session data inclusing a controller instance, or null.
	 *
	 * @param path a controller path starting with "/" (e.g., "/my-controller")
	 * @param request a request
	 * @return a controller script and sub session data inclusing a controller instance, or null.
	 */
	static public ControllerScriptAndSubSession get( final String path, final HttpServletRequest request ) {
		final ControllerServlet servlet = ControllerServletLoader.getServlet( path );
		if( servlet != null ) {
			try {
				return servlet.handle( request );
			} catch( final Exception e ) {
				logger.debug( "Failed to handle a request", e );
			}
		}
		return null;
	}
}
