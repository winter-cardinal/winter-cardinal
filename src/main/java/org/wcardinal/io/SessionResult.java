/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

public class SessionResult {
	final SubSession subSession;
	final String subSessionId;
	final boolean isNew;

	SessionResult( final String subSessionId, final SubSession subSession, final boolean isNew ){
		this.isNew = isNew;
		this.subSession = subSession;
		this.subSessionId = subSessionId;
	}

	public SubSession getSubSession(){
		return subSession;
	}

	public boolean isNew(){
		return isNew;
	}

	public String getSubSessionId(){
		return subSessionId;
	}
}
