/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.onidlecheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.controller.ControllerIo;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnIdleCheck;

@Controller
public class OnIdleCheckController {
	static final Logger logger = LoggerFactory.getLogger(OnIdleCheckController.class);

	@OnIdleCheck
	long idleCheck( final ControllerIo io ) {
		logger.info( "{} {} idle {} connection ({}, {})", io.getSessionId(), io.getSubSessionId(), io.getIdleTime(), io.hasConnection(), io.hadConnection() );
		if( io.hadConnection() ) {
			return io.hasConnection() ? 1000 : -1;
		} else {
			return io.getMaximumIdleTime();
		}
	}
}
