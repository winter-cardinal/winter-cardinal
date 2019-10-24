/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

class TaskInternalImplCanceled implements TaskInternal {
	final TaskController controller;
	final long id;
	final boolean hasResult;

	public TaskInternalImplCanceled( final TaskController controller, final long id, final boolean hasResult ){
		this.controller = controller;
		this.id = id;
		this.hasResult = hasResult;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public boolean cancel( final String type ){
		return false;
	}

	@Override
	public boolean isCanceled(){
		return true;
	}

	public boolean isDone() {
		return true;
	}

	@Override
	public boolean complete( final Object result, final Runnable runnable ) {
		return false;
	}

	@Override
	public Runnable getRunnable() {
		return null;
	}

	@Override
	public boolean hasResult() {
		return hasResult;
	}

	@Override
	public TaskController getController() {
		return controller;
	}

	@Override
	public String getResultType() {
		return TaskResultType.CANCELED;
	}
}
