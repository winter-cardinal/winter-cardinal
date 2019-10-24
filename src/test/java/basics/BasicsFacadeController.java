/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SInteger;

@Controller
public class BasicsFacadeController {
	//------------------------------------
	// CONTROLLER
	//------------------------------------
	@Autowired
	ControllerFacade facade;

	@Callable
	void start(){
		count.set(0);
		total.set(0);
		facade.timeout("a", 0, 12);
	}

	@Callable
	void restart(){
		facade.timeout("a", 0, 12);
	}

	@Autowired
	SInteger count;

	@Autowired
	SInteger total;

	@OnTime( "*" )
	void onTime( final int number ){
		count.getAndIncrement();
	}

	@OnTime
	void a( final int number ){
		facade.timeout("b", 0, number + 1);
	}

	@OnTime
	void b( final int number ){
		facade.timeout("c", 0, number + 1);
	}

	@OnTime
	void c( final int number ){
		total.set(number);
	}

	//------------------------------------
	// PAGE
	//------------------------------------
	@Autowired
	BasicsFacadePage page;
	@Callable
	boolean start_page(){
		if( page.facade.isShown() ){
			page.facade.hide();
			if( page.facade.isHidden() ){
				return true;
			}
		}

		return false;
	}

	//------------------------------------
	// POPUP
	//------------------------------------
	@Autowired
	BasicsFacadePopup popup;
	@Callable
	boolean start_popup(){
		if( popup.facade.isShown() ){
			popup.facade.hide();
			if( popup.facade.isHidden() ){
				return true;
			}
		}

		return false;
	}
}
