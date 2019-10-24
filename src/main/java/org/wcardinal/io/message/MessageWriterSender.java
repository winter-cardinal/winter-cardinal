/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

import java.io.IOException;

public interface MessageWriterSender {
	void send( CharSequence message, boolean isLast ) throws IOException;
}
