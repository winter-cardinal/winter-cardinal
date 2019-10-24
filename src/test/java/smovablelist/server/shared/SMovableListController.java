/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package smovablelist.server.shared;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class SMovableListController extends AbstractController {
	@Autowired
	SMovableListSharedComponent component;
}
