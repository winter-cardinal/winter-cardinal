/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util;

public class Reference<T> {
	T referent;

	public Reference(){
		this.referent = null;
	}

	public Reference( final T referent ) {
		this.referent = referent;
	}

	public T get(){
		return this.referent;
	}

	public T set( final T referent ){
		final T result = this.referent;
		this.referent = referent;
		return result;
	}
}
