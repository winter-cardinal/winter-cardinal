/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.util.Objects;

public class TrackingIds {
	private long expected;
	private TrackingId trackingId;

	public TrackingIds( final long expected, final TrackingId trackingId ){
		this.expected = expected;
		this.trackingId = trackingId;
	}

	public boolean isHead(){
		return Objects.equals( trackingId.current(), expected );
	}
}
