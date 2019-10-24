/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import org.wcardinal.exception.NotReadyException;

/**
 * Provides additional functions of visibility controllers.
 */
public abstract class AbstractVisibilityController extends AbstractController implements VisibilityControllerContext {
	@Override
	public String getDisplayName(){
		if( controllerContext instanceof VisibilityControllerContext ) {
			return ((VisibilityControllerContext)controllerContext).getDisplayName();
		} else {
			throw new NotReadyException();
		}
	}

	@Override
	public void setDisplayName( final String displayName ){
		if( controllerContext instanceof VisibilityControllerContext ) {
			((VisibilityControllerContext)controllerContext).setDisplayName( displayName );
		} else {
			throw new NotReadyException();
		}
	}
}
