/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import java.io.IOException;

public class SharedEndpointSessionLockResult implements EndpointSessionLockResult {
	boolean isFirst;
	final String subSessionId;
	final EndpointSessionLockResult lockResult;

	public SharedEndpointSessionLockResult( final String subSessionId, final EndpointSessionLockResult lockResult ){
		this.isFirst = true;
		this.subSessionId = subSessionId;
		this.lockResult = lockResult;
	}

	@Override
	public void cancel() {
		lockResult.cancel();
	}

	@Override
	public void unlock() {
		lockResult.unlock();
	}

	@Override
	public void send( final CharSequence message, final boolean isLast ) throws IOException {
		if( isFirst ) {
			isFirst = false;
			lockResult.send( subSessionId+":"+message, isLast );
		} else {
			lockResult.send( message, isLast );
		}
	}
}
