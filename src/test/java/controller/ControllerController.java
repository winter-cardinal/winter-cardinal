/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.PageFactory;
import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class ControllerController {
	@Autowired
	ControllerComponent component;

	@Autowired
	ControllerPage page;

	@Autowired
	ControllerPopup popup;

	@Autowired
	ComponentFactory<ControllerComponent> componentFactory;

	@Autowired
	PageFactory<ControllerComponent> pageFactory;

	@Autowired
	PopupFactory<ControllerComponent> popupFactory;

	@Autowired
	ControllerFacade facade;

	@Callable
	boolean getActivePage_check() {
		return ( facade.getActivePage() == this.page );
	}

	@Callable
	boolean getActivePage_null_check() {
		return ( facade.getActivePage() == null );
	}
}
