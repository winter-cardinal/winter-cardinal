/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.measure;

import java.util.HashMap;
import java.util.Map;

import org.wcardinal.util.doc.InWork;

@InWork
public class Counters {
	private final static Map<String, Counter> idToCounter = new HashMap<>();

	public synchronized static Counter get( final String id ) {
		Counter counter = Counters.idToCounter.get( id );
		if( counter == null ) {
			counter = new Counter( id );
			Counters.idToCounter.put( id, counter );
		}
		return counter;
	}

	public static void count( final String id, final long number ) {
		get( id ).count( number );
	}
}
