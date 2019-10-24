/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.historical;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.annotation.Historical;

@Controller
public class SListController extends AbstractController {
	@Autowired @Historical
	SList<Integer> field_list;
}
