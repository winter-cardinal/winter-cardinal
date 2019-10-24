/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.PageFacade;
import org.wcardinal.controller.annotation.DisplayName;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnHide;
import org.wcardinal.controller.annotation.OnShow;
import org.wcardinal.controller.annotation.Page;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SInteger;

@Page
@DisplayName( "title-0" )
public class BasicsPage {
	@Autowired
	PageFacade facade;

	@Autowired
	SInteger shown;

	@Autowired
	SInteger hidden;

	@OnCreate
	void onCreate(){
		shown.set( 0 );
		hidden.set( 0 );
	}

	@OnShow
	void onShow(){
		shown.incrementAndGet();
	}

	@OnShow
	@Unlocked
	void onShowUnlocked(){
		shown.incrementAndGet();
	}

	@OnHide
	void onHide(){
		hidden.incrementAndGet();
	}

	@OnHide
	@Unlocked
	void onHideUnlocked(){
		hidden.incrementAndGet();
	}
}
