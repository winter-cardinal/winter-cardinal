/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

class TaskInternalImpl implements TaskInternal {
	final TaskController controller;
	final long id;
	boolean isCanceled;
	boolean isCompleted;
	Object result;
	Runnable runnable;
	String resultType;

	public TaskInternalImpl( final TaskController controller, final long id ){
		this.controller = controller;
		this.id = id;
		this.isCanceled = false;
		this.isCompleted = false;
		this.result = null;
		this.runnable = null;
		this.resultType = TaskResultType.SUCCEEDED;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public boolean cancel( final String type ){
		updateState();
		if( isCompleted == false ) {
			isCanceled = true;
			isCompleted = true;
			resultType = type;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isCanceled(){
		updateState();
		return isCanceled;
	}

	void updateState(){
		if( isCanceled != true && controller.$id.equals( id ) != true ){
			isCanceled = true;
			isCompleted = true;
			resultType = TaskResultType.CANCELED;
		}
	}

	public boolean isDone() {
		updateState();
		return isCompleted;
	}

	@Override
	public boolean complete( final Object result, final Runnable runnable ) {
		updateState();
		if( isCompleted != true ) {
			isCompleted = true;
			this.runnable = runnable;
			this.result = result;
			resultType = TaskResultType.SUCCEEDED;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Runnable getRunnable(){
		return runnable;
	}

	@Override
	public boolean hasResult() {
		return true;
	}

	@Override
	public TaskController getController() {
		return controller;
	}

	@Override
	public String getResultType() {
		return resultType;
	}
}
