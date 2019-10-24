/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.Map;

import org.wcardinal.controller.data.internal.SChange;

public class SChangeInfo extends Info<SChange, SChangeInfo> {
	public SChangeInfo( final Map<String, SChange> nameToData, final Map<String, SChangeInfo> nameToInfo ) {
		super( nameToData, nameToInfo );
	}
}
