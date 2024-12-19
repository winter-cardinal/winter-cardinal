/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

public interface CallSMapsPut {
	void putVoidIfExists( final String key );
	void putErrorIfExists( final String key, final String error );
	void putResultIfExists( final String key, final Object result );
}
