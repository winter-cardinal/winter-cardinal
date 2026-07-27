/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

public interface SPatch {
	int getWeight();
	void serialize( JsonGenerator gen ) throws JacksonException;
	boolean isReset();
}
