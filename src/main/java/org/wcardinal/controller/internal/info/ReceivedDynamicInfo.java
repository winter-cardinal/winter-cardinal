/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using=ReceivedDynamicInfoDeserializer.class )
public class ReceivedDynamicInfo extends Info<DynamicDataJsonNode, ReceivedDynamicInfo> {
	public ReceivedDynamicInfo( final Map<String, DynamicDataJsonNode> nameToData, final Map<String, ReceivedDynamicInfo> nameToInfo ) {
		super( nameToData, nameToInfo );
	}
}
