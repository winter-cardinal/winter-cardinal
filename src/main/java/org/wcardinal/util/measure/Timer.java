/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.measure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.util.doc.InWork;

@InWork
public class Timer {
	final Logger logger = LoggerFactory.getLogger(Timer.class);

	final String id;
	long count = 0;
	long took = 0;
	long max = Long.MIN_VALUE;
	long min = Long.MAX_VALUE;
	long previous = System.currentTimeMillis();

	long start = 0;

	Timer( final String id ) {
		this.id = id;
	}

	synchronized void start() {
		start = System.currentTimeMillis();
	}

	synchronized void end() {
		long end = System.currentTimeMillis();
		took( end - start );
	}

	private synchronized void took( final long took ) {
		count += 1;
		this.took += took;
		max = Math.max( max, took );
		min = Math.min( min, took );
		final long now = System.currentTimeMillis();
		final long elapsed = now - previous;
		if( 1000 < elapsed ) {
			double f = 1.0 / count;
			logger.info( "{} min {} ms, avg {} ms, max {} ms, total {} ms, count {}, interval {} ms", id, min, String.format("%.3f", (double)(this.took * f)), max, this.took, count, elapsed * f );
			count = 0;
			this.took = 0;
			max = Long.MIN_VALUE;
			min = Long.MAX_VALUE;
			previous = now;
		}
	}
}
