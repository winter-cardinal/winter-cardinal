/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.server_shared_readonly;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Controller;

@Controller
public class SListController extends AbstractController {
	@Autowired
	SListSharedReadOnlyComponent component;
}
