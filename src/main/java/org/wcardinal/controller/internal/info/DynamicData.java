/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

public class DynamicData<T> {
	public final long revision;
	public final int type;
	public final T data;

	protected DynamicData( final long revision, final int type, final T data ) {
		this.revision = revision;
		this.type = type;
		this.data = data;
	}
}
