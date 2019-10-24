/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import java.io.IOException;

public interface EndpointSessionLockResult {
	void cancel();
	void unlock();
	void send( final CharSequence message, final boolean isLast ) throws IOException;
}
