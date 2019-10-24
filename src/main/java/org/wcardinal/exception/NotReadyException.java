/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.exception;

/**
 * Thrown to indicate a method which is not ready has been invoked.
 * Invoking {@link org.wcardinal.controller.AbstractController}'s methods from the methods annotated with the javax.annotation.PostConstruct is, typically, the root cause of this exception.
 * If this is the case, please consider using {@link org.wcardinal.controller.annotation.OnCreate} instead.
 *
 * @see org.wcardinal.controller.annotation.OnCreate
 */
public class NotReadyException extends RuntimeException {
	private static final long serialVersionUID = 4205139096116180187L;

	public NotReadyException(){
		super();
	}

	public NotReadyException( final String message ){
		super( message );
	}
}
