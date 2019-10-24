/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

public class TrackingId {
	private long current = 0;
	private long next = 0;

	public TrackingId(){}

	public synchronized long next(){
		next += 1;
		return next;
	}

	public synchronized long move( long next ){
		current = Math.max(current, next);
		return current;
	}

	public synchronized long current(){
		return current;
	}
}
