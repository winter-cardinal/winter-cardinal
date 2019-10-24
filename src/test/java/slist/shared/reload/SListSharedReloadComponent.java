/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.shared.reload;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.SharedComponent;
import org.wcardinal.controller.data.SList;

@SharedComponent
public class SListSharedReloadComponent {
	@Autowired
	SList<Long> list;

	@OnCreate
	void init() {
		list.addAll(Arrays.asList( 0L, 1L, 2L ));
	}
}
