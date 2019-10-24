/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package slist.browser;

import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.data.SList;

@Controller
public class SListController extends AbstractController {
	@Constant
	static final int SIZE = 100;

	@Autowired
	SList<Integer> field_list;

	@Callable
	boolean check() {
		if( field_list.size() != SIZE ) return false;

		final ListIterator<Integer> iterator = field_list.listIterator();
		while( iterator.hasNext() ) {
			if( iterator.nextIndex() != iterator.next() ) return false;
		}

		return true;
	}
}
