/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.PopupFacade;
import org.wcardinal.controller.annotation.Popup;

@Popup
public class BasicsFacadePopup {
	@Autowired
	PopupFacade facade;
}
