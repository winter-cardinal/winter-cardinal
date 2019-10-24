/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.Map;

public class RejectionInfo extends Info<Boolean, RejectionInfo> {
	public RejectionInfo( final Map<String, Boolean> nameToData, final Map<String, RejectionInfo> nameToInfo ) {
		super( nameToData, nameToInfo );
	}

	public static RejectionInfo create( final Map<String, Boolean> nameToData, final Map<String, RejectionInfo> nameToInfo ) {
		if( nameToData == null && nameToInfo == null ) return null;
		return new RejectionInfo( nameToData, nameToInfo );
	}
}
