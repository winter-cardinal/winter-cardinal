/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.Map;

class ConcurrentRunnable implements Runnable {
	final long id;
	final Map<Long, ConcurrentData> idToData;
	final boolean isAutoRemove;

	public ConcurrentRunnable( final long id, final Map<Long, ConcurrentData> idToData, final boolean isAutoRemove ) {
		this.id = id;
		this.idToData = idToData;
		this.isAutoRemove = isAutoRemove;
	}

	@Override
	public void run() {
		final ConcurrentData data = ( isAutoRemove ? idToData.remove( id ) : idToData.get( id ) );
		if( data != null ){
			data.run( id, this );
		}
	}
}
