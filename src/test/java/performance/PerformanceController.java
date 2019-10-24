/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package performance;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class PerformanceController {
	@Autowired @NonNull
	SList<byte[]> slist;

	@Autowired
	ControllerFacade facade;

	@Callable
	void start() {
		facade.interval( "update", 5 );
	}


	byte[] makeByte( int size ) {
		byte[] result = new byte[ size ];
		for( int i=0; i<size; ++i ) {
			result[ i ] = (byte) i;
		}
		return result;
	}

	@OnTime
	void update() {
		int SIZE = 1000;
		slist.lock();
		try {
			for( int i=0; i<10; ++i ) {
				int makeSize = (int) Math.floor( ( 1 + Math.random() ) * 1000 );
				if( slist.size() <= 0 || Math.random() < 0.5 ) {
					slist.add( makeByte( makeSize ) );
					if( SIZE < slist.size() ) {
						slist.remove( 0 );
					}
				} else {
					slist.set( (int)Math.floor( Math.random() * slist.size() ), makeByte( makeSize ) );
				}
			}
		} finally {
			slist.unlock();
		}
	}
}
