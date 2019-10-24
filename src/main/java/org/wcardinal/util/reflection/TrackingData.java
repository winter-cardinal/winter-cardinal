/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.util.HashMap;
import java.util.Map;

public class TrackingData {
	Map<AbstractMethodWrapper<?>, Long> ids = new HashMap<>();

	public TrackingData(){

	}

	public Long put( final AbstractMethodWrapper<?> wrapper, Long id ){
		return ids.put(wrapper, id);
	}

	public Long get( final AbstractMethodWrapper<?> wrapper ){
		return ids.get( wrapper );
	}
}
