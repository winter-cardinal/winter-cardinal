/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onchange.fail10;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.data.SString;
import org.wcardinal.controller.data.SMovableList.Move;
import org.wcardinal.controller.data.SList.Update;

@Controller
public class SyntaxCheckController {
	@Autowired
	SString bar;

	@Autowired
	SyntaxCheckComponent component;

	@OnChange( "component.movable_list" )
	void onChangeB10( final SortedMap<Object, String> added, final SortedMap<Integer, String> removed, final SortedMap<Integer, Update<String>> updated, final Collection<Move<String>> newMoved, final List<Move<String>> oldMoved ){}
}
