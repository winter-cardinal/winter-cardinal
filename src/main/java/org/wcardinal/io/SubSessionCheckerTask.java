/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import java.util.concurrent.atomic.AtomicBoolean;

public class SubSessionCheckerTask implements Runnable {
	private final SubSessionCheckerParent parent;
	private final AtomicBoolean isCanceled;

	public SubSessionCheckerTask( final SubSessionCheckerParent parent ) {
		this.parent = parent;
		this.isCanceled = new AtomicBoolean( false );
	}

	public void cancel() {
		isCanceled.set( true );
	}

	public boolean isCanceled() {
		return isCanceled.get();
	}

	@Override
	public void run() {
		if( ! isCanceled.get() ) {
			parent.check( this );
		}
	}
}
