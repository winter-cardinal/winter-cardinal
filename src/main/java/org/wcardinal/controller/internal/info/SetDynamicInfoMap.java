/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.HashMap;
import java.util.Map;

public class SetDynamicInfoMap {
	public Map<String, SChangeInfo> nameToSChange = null;
	public Map<String, RejectionInfo> nameToRejection = null;

	public SetDynamicInfoMap() {}

	public void put( final String name, final SetDynamicInfo info ) {
		if( info != null ) {
			if( info.schange != null ) {
				if( nameToSChange == null ) {
					nameToSChange = new HashMap<>();
				}
				nameToSChange.put(name, info.schange);
			}

			if( info.rejection != null ) {
				if( nameToRejection == null ) {
					nameToRejection = new HashMap<>();
				}
				nameToRejection.put(name, info.rejection);
			}
		}
	}

	public static SetDynamicInfoMap put( SetDynamicInfoMap map, final String name, final SetDynamicInfo info ) {
		if( info != null ) {
			if( map == null ) {
				map = new SetDynamicInfoMap();
			}
			map.put( name, info );
		}
		return map;
	}
}
