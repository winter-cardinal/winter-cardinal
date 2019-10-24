/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

public class RejectedSetDynamicInfo {
	public final SetDynamicInfo info;
	public final ReceivedDynamicInfo receivedInfo;
	public final long createdAt;

	public RejectedSetDynamicInfo( final SetDynamicInfo info, final ReceivedDynamicInfo receivedInfo ) {
		this.info = info;
		this.receivedInfo = receivedInfo;
		this.createdAt = System.currentTimeMillis();
	}
}
