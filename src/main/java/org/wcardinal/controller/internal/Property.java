/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

public enum Property {
	SHARED,			// 1
	FACTORY,		// 2
	READ_ONLY,		// 4
	HISTORICAL,		// 8
	NON_NULL,		// 16
	UNINITIALIZED,	// 32
	SOFT,			// 64
	AJAX,			// 128
	LOCAL,			// 256
	PRIMARY			// 512
}
