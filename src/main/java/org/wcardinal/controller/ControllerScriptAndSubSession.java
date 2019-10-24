/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import org.wcardinal.io.SubSession;

public class ControllerScriptAndSubSession {
	public final String script;
	public final SubSession subSession;

	public ControllerScriptAndSubSession( final String script, final SubSession subSession ){
		this.script = script;
		this.subSession = subSession;
	}
}
