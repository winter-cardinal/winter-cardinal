/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onchange.fail2;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.SMap;
import org.wcardinal.controller.data.SMovableList;
import org.wcardinal.controller.data.SNavigableMap;
import org.wcardinal.controller.data.SString;

@Component
public class SyntaxCheckComponent {
	@Autowired
	SString name;

	@Autowired
	SList<String> list;

	@Autowired
	SMovableList<String> movable_list;

	@Autowired
	SMap<String> map;

	@Autowired
	SNavigableMap<String> navigable_map;
}
