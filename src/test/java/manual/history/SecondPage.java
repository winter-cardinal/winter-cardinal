/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.DisplayNameMessage;
import org.wcardinal.controller.annotation.OnShow;
import org.wcardinal.controller.annotation.Page;
import org.wcardinal.controller.data.SLong;

@Page
@DisplayNameMessage( "page.second" )
public class SecondPage {
	final Logger logger = LoggerFactory.getLogger(SecondPage.class);

	@Autowired
	SLong time;

	@OnShow
	void onShow(){
		logger.info("On-show");
	}
}
