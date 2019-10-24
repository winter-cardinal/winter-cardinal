/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

public interface SPatch {
	int getWeight();
	void serialize( JsonGenerator gen ) throws IOException;
	boolean isReset();
}
