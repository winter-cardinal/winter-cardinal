/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.controller.data.SLockable;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

public interface SBase<T> extends SLockable {
	int getType();

	long getRevision();

	void setReadOnly(boolean isReadOnly);
	boolean isReadOnly();

	void setNonNull(boolean isNonNull);
	boolean isNonNull();

	// A soft SBase sets its value to null immediately after receiving acknowledge messages.
	void setSoft(boolean isSoft);
	boolean isSoft();
	void compact( long authorizedRevision );

	// A weak SBase sets its value to null immediately after sending it without waiting acknowledge messages.
	void setWeak( boolean isWeak );
	boolean isWeak();

	// A loose SBase accepts update messages which revisions are greater than or equals to the revision it has.
	void setLoose( boolean isLoose );
	boolean isLoose();

	boolean isInitialized();
	void uninitialize();

	void setParent( SParent parent );

	Object pack( SData sdata );
	SChange unpack( JsonNode valueNode, long revision, SData sdata ) throws Exception;
	void override( long revision );
	void onAuthorized( long authorizedRevision );

	void setLock(final AutoCloseableReentrantLock lock);
}
