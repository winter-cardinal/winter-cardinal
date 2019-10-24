/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.cast;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.data.SClass;
import org.wcardinal.controller.data.SList;

@Controller
public class CastController {
	@Autowired
	SList<CastClass> field_list;

	@Autowired
	SClass<CastClass> field_class;
}
