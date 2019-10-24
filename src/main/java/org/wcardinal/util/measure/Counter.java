/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.measure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.util.doc.InWork;

@InWork
public class Counter {
	final Logger logger = LoggerFactory.getLogger(Counter.class);

	final String id;
	long count = 0;
	long total = 0;
	long max = Long.MIN_VALUE;
	long min = Long.MAX_VALUE;
	long previous = System.currentTimeMillis();

	Counter( final String id ) {
		this.id = id;
	}

	public synchronized void count( final long number ) {
		count += 1;
		total += number;
		max = Math.max(max, number);
		min = Math.min(min, number);
		final long now = System.currentTimeMillis();
		final long elapsed = now - previous;
		if( 1000 < elapsed ) {
			double f = 1.0 / count;
			logger.info( "{} min {}, avg {}, max {}, total {}, count {}, interval {} ms", id, min, Math.round(total*f), max, total, count, Math.round(elapsed*f) );
			count = 0;
			total = 0;
			max = Integer.MIN_VALUE;
			min = Integer.MAX_VALUE;
			previous = now;
		}
	}
}
