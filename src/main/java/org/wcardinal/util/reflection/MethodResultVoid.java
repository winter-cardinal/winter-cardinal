/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

public class MethodResultVoid<T> implements MethodResult<T> {
	public static final MethodResultVoid<Object> INSTANCE = new MethodResultVoid<Object>();

	@SuppressWarnings("unchecked")
	public static <T> MethodResultVoid<T> getInstance() {
		return (MethodResultVoid<T>) INSTANCE;
	}

	public MethodResultVoid(){}
}
