/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

public class MethodResultData<T> implements MethodResult<T> {
	public final T data;

	public MethodResultData( final T data ){
		this.data = data;
	}
}
