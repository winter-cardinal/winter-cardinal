/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package shared.trigger_direct;

import java.util.concurrent.atomic.AtomicInteger;

import org.wcardinal.controller.AbstractComponent;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.annotation.SharedComponent;

@SharedComponent
public class SharedTriggerComponent extends AbstractComponent {
	@Callable
	void start( int maxCount ) {
		count.set( 0 );
		interval( "trigger", 500, 50, maxCount );
	}

	final AtomicInteger count = new AtomicInteger( 0 );

	@OnTime
	void trigger( int maxCount ) {
		this.triggerDirect( "event", count.getAndIncrement() );
		if( maxCount <= count.get() ) {
			cancelAll();
		}
	}
}
