/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.measure;

import java.util.HashMap;
import java.util.Map;

import org.wcardinal.util.doc.InWork;

@InWork
public class Timers {
	private final static Map<String, Timer> idToTimer = new HashMap<>();

	public synchronized static Timer get( final String id ) {
		Timer timer = Timers.idToTimer.get( id );
		if( timer == null ) {
			timer = new Timer( id );
			Timers.idToTimer.put( id, timer );
		}
		return timer;
	}

	public static void start( final String id ) {
		get( id ).start();
	}

	public static void end( final String id ) {
		get( id ).end();
	}
}
