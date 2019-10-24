/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.Map;

import org.wcardinal.controller.data.internal.SData;
import org.wcardinal.controller.internal.info.DynamicDataObject;
import org.wcardinal.controller.internal.info.SetDynamicDataMap;

public interface ControllerDynamicInfoHandler {
	void handle( Object origin, SetDynamicDataMap map );
	void handle( Object origin, Map<String, SData> nameToSData, Map<String, DynamicDataObject> nameToData, final long senderId );
}
