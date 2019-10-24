/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

public class TaskAbortException extends RuntimeException {
	private static final long serialVersionUID = 6884739395605869192L;

	public TaskAbortException( final String reason ) {
		super( reason );
	}
}
