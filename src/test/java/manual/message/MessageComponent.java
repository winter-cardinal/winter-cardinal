/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.message;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractComponent;
import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SClass;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.annotation.NonNull;

@Component
public class MessageComponent extends AbstractComponent {
	@Autowired
	SList<String> list;

	@Autowired
	ComponentFactory<MessageFactoryComponent> factory;

	@Autowired
	SClass<long[]> large;

	@Autowired @NonNull
	SInteger counter;

	@Autowired @NonNull
	SInteger tick;

	@OnCreate
	void init() {
		interval( "count-up",  0, 10000 );
		interval( "tick",  0, 1000 );
	}

	@OnTime
	@Locked
	void tick() {
		tick.incrementAndGet();
	}

	@OnTime( "count-up" )
	@Locked
	void countUp() {
		// list.clearAndAdd( ""+System.currentTimeMillis() );

		// factory.clear();
		// factory.create();

		final long now = System.currentTimeMillis();
		final long[] large = new long[ 40000 ];
		for( int i=0; i<large.length; ++i ) {
			large[ i ] = (long) now + i;
		}
		this.large.set( large );

		counter.incrementAndGet();
	}
}
