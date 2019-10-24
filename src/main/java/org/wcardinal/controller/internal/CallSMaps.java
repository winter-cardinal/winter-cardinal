/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

public interface CallSMaps extends CallSMapsPut {
	void removeOrigin( Controller origin );
	void removeResult( String key );
	void removeResultIfNotExits( String key );
	boolean containsResultKey( String key );
}
