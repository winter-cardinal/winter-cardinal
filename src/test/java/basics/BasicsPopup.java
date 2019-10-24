/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.PopupFacade;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnHide;
import org.wcardinal.controller.annotation.OnShow;
import org.wcardinal.controller.annotation.Popup;
import org.wcardinal.controller.data.SInteger;

@Popup
public class BasicsPopup {
	@Autowired
	PopupFacade facade;

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

	@OnHide
	void onHide(){
		hidden.incrementAndGet();
	}
}
