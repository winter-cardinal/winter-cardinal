/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.ArrayDeque;
import java.util.Iterator;

import org.wcardinal.controller.internal.info.RejectedSetDynamicInfo;

public class RejectedSetDynamicInfos extends ArrayDeque<RejectedSetDynamicInfo> {
	private static final long serialVersionUID = 8934637772358319644L;

	public void cleanup( final long interval ) {
		if( isEmpty() != true ) {
			final long now = System.currentTimeMillis();
			final Iterator<RejectedSetDynamicInfo> iterator = iterator();
			while( iterator.hasNext() ) {
				final RejectedSetDynamicInfo rejectedInfo = iterator.next();
				if( interval <= now - rejectedInfo.createdAt ) {
					iterator.remove();
				}
			}
		}
	}
}
