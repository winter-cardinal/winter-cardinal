/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package callable.heavy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Timeout;

@Controller
public class CallableHeavyController {
	final Map<Integer, AtomicInteger> indexToCount = new HashMap<>();

	@Callable
	void reset() {
		indexToCount.clear();
	}

	@Callable
	@Timeout( 10000 )
	void test( int index ) {
		if( indexToCount.containsKey( index ) ) {
			indexToCount.get( index ).incrementAndGet();
		} else {
			indexToCount.put( index, new AtomicInteger( 1 ) );
		}
	}

	@Callable
	boolean check(){
		for( final AtomicInteger value: indexToCount.values() ) {
			if( 1 < value.get() ) return false;
		}
		return true;
	}
}
