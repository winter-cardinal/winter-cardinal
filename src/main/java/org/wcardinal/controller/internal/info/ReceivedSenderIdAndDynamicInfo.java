/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape=Shape.ARRAY)
public class ReceivedSenderIdAndDynamicInfo {
	public long senderId;
	public ReceivedDynamicInfo dynamicInfo;

	@JsonCreator
	ReceivedSenderIdAndDynamicInfo(
		@JsonProperty( "senderId" ) final long senderId,
		@JsonProperty( "dynamicInfo" ) final ReceivedDynamicInfo dynamicInfo
	){
		this.senderId = senderId;
		this.dynamicInfo = dynamicInfo;
	}
}
