/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.wcardinal.controller.ControllerScriptAndSubSession;
import org.wcardinal.io.SubSession;

public class ControllerServletHandleResult extends ControllerScriptAndSubSession {
	public ControllerServletHandleResult( final String script, final SubSession subSession ){
		super( script, subSession );
	}
}
