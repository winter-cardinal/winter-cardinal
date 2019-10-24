/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.HashMap;
import java.util.Map;

import org.wcardinal.controller.data.internal.SChange;

public class SetDynamicDataMap {
	public Map<String, SChange> nameToSChange = null;
	public Map<String, Boolean> nameToRejection = null;

	public SetDynamicDataMap() {}

	public void put( final String name, final SChange schange ) {
		if( schange != null ) {
			if( nameToSChange == null ) {
				nameToSChange = new HashMap<>();
			}
			nameToSChange.put(name, schange);
		}
	}

	public void put( final String name, final boolean rejection ) {
		if( nameToRejection == null ) {
			nameToRejection = new HashMap<>();
		}
		nameToRejection.put(name, rejection);
	}

	public static SetDynamicDataMap put( final SetDynamicDataMap map, final String name, final SChange schange ) {
		if( schange != null ) {
			if( map == null ) {
				SetDynamicDataMap newMap = new SetDynamicDataMap();
				newMap.put( name, schange );
				return newMap;
			} else {
				map.put( name, schange );
				return map;
			}
		} else {
			return map;
		}
	}

	public static SetDynamicDataMap put( SetDynamicDataMap map, final String name, final boolean rejection ) {
		if( map == null ) {
			map = new SetDynamicDataMap();
		}
		map.put( name, rejection );
		return map;
	}
}
