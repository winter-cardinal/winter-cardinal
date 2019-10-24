/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package partial.whole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Unlocked;

@Controller
public class PartialController extends AbstractController {
	@Callable
	@Unlocked
	public List<Long> generate( int size ){
		Uninterruptibles.sleepUninterruptibly(Math.round(Math.random()*10), TimeUnit.MILLISECONDS);
		final List<Long> times = new ArrayList<>();
		final long now = System.currentTimeMillis();
		for( int i=0; i<size; ++i ) {
			times.add( now );
		}
		return times;
	}
}
