/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@JsonFormat( shape=Shape.ARRAY )
public class SenderIdAndDynamicInfo {
	public long senderId;
	public DynamicInfo dynamicInfo;

	public SenderIdAndDynamicInfo( final long senderId, final DynamicInfo dynamicInfo ) {
		this.senderId = senderId;
		this.dynamicInfo = dynamicInfo;
	}
}
