/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

public interface SubSessionChecker {
	void start();
	void check();
	void stop();
}
