/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class BasicsTriggerDirectController extends AbstractController {
	@Callable
	void trigger_request( final String eventName, final int value ){
		this.triggerDirect(eventName, value);
	}

	@Callable
	void trigger_requests( final String eventName, final int value, final int count ){
		for( int i=0; i<count; ++i ) {
			this.triggerDirect(eventName, value * i);
		}
	}
}
