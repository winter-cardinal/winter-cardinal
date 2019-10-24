/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package worker;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.annotation.NonNull;

@Controller
public class WorkerController {
	@Autowired @NonNull
	SList<Long> slist;

	@Autowired
	ControllerFacade facade;

	@OnCreate
	void init() {
		facade.interval( "update", 5 );
	}

	@OnTime
	void update() {
		int SIZE = 1000;
		slist.lock();
		try {
			long now = System.currentTimeMillis();
			for( int i=0; i<10; ++i ) {
				if( slist.size() <= 0 || Math.random() < 0.5 ) {
					slist.add( now );
					if( SIZE < slist.size() ) {
						slist.remove( 0 );
					}
				} else {
					slist.set( (int)Math.floor( Math.random() * slist.size() ), now );
				}
			}
		} finally {
			slist.unlock();
		}
	}
}
