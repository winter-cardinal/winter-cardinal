/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import org.wcardinal.controller.internal.RootController;

public class SubSessionData {
	final SubSession subSession;

	public SubSessionData(final SubSession subSession) {
		this.subSession = subSession;
	}

	public RootController getRootController(){
		return subSession.getRootController();
	}

	@Override
	public String toString(){
		return subSession.toString();
	}
}
