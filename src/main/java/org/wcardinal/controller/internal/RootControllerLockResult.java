/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.wcardinal.controller.internal.info.SenderIdAndDynamicInfo;

public class RootControllerLockResult {
	public final SenderIdAndDynamicInfo senderIdAndDynamicInfo;
	public final RootControllerLock lock;

	public RootControllerLockResult( final RootControllerLock lock, final SenderIdAndDynamicInfo senderIdAndDynamicInfo ) {
		this.senderIdAndDynamicInfo = senderIdAndDynamicInfo;
		this.lock = lock;
	}
}
