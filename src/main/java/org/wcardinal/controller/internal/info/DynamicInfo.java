/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize( using=DynamicInfoSerializer.class )
public class DynamicInfo extends Info<DynamicDataObject, DynamicInfo> {
	public DynamicInfo() {
		super();
	}

	public DynamicInfo( final Map<String, DynamicDataObject> nameToData, final Map<String, DynamicInfo> nameToInfo ) {
		super( nameToData, nameToInfo );
	}

	public static DynamicInfo create( final Map<String, DynamicDataObject> nameToData, final Map<String, DynamicInfo> nameToInfo ) {
		if( nameToData != null || nameToInfo != null ) {
			return new DynamicInfo( nameToData, nameToInfo );
		} else {
			return null;
		}
	}
}
