/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package sync;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.KeepAlive;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.SMap;
import org.wcardinal.controller.data.SMovableList;
import org.wcardinal.controller.data.SNavigableMap;

@Controller(keepAlive=@KeepAlive(ping=-1))
public class SyncController extends AbstractController {
	@Autowired
	SList<String> field_list;

	@Autowired
	SMovableList<String> field_movable_list;

	@Autowired
	SMap<String> field_map;

	@Autowired
	SNavigableMap<String> field_navigable_map;

	@Callable
	boolean check() {
		return (
			field_list.contains( null ) != true &&
			field_movable_list.contains( null ) != true &&
			field_map.containsValue( null ) != true &&
			field_navigable_map.containsValue( null ) != true
		);
	}
}
