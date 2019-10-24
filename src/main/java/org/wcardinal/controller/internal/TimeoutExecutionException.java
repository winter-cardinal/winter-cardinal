/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

public class TimeoutExecutionException extends RuntimeException {
	private static final long serialVersionUID = 2171538056261862199L;
	TimeoutExecutionException( final Throwable e ) {
		super( e );
	}
}
