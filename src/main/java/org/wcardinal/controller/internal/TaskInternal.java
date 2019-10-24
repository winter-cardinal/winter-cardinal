/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

interface TaskInternal {
	long getId();
	boolean isCanceled();
	boolean isDone();
	boolean cancel( String type );
	boolean complete( final Object result, final Runnable runnalbe );
	Object getResult();
	Runnable getRunnable();
	String getResultType();
	boolean hasResult();
	TaskController getController();
}
