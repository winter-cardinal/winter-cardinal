/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax.method.onchange.success;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.data.SString;
import org.wcardinal.controller.data.SMovableList.Move;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.SMap;
import org.wcardinal.controller.data.SList.Update;

@Controller
public class SyntaxCheckController {
	@Autowired
	SString bar;

	@Autowired
	SyntaxCheckComponent component;

	@OnChange( "bar" )
	void onChangeA1(){}

	@OnChange( "bar" )
	void onChangeA2( final String value ){}

	@OnChange( "bar" )
	void onChangeA2( final Object value ){}

	@OnChange( "bar" )
	void onChangeA3( final String newValue, final String oldValue ){}

	@OnChange( "bar" )
	void onChangeA3( final Object newValue, final Object oldValue ){}

	@OnChange( "component.name" )
	void onChangeA4(){}

	@OnChange( "component.name" )
	void onChangeA5( final String value ){}

	@OnChange( "component.name" )
	void onChangeA5( final Object value ){}

	@OnChange( "component.name" )
	void onChangeA6( final String newValue, final String oldValue ){}

	@OnChange( "component.name" )
	void onChangeA6( final Object newValue, final Object oldValue ){}

	@OnChange( "component.list" )
	void onChangeA7(){}

	@OnChange( "component.list" )
	void onChangeA7( final SortedMap<Integer, String> added ){}

	@OnChange( "component.list" )
	void onChangeA7( final SortedMap<Integer, String> added, final Object removed ){}

	@OnChange( "component.list" )
	void onChangeA7( final SortedMap<Integer, String> added, final SortedMap<Integer, String> removed ){}

	@OnChange( "component.list" )
	void onChangeA7( final SortedMap<Integer, String> added, final SortedMap<Integer, String> removed, final Object updated ){}

	@OnChange( "component.list" )
	void onChangeA7( final SortedMap<Integer, String> added, final SortedMap<Integer, String> removed, final SortedMap<Integer, SList.Update<String>> updated ){}

	@OnChange( "component.list" )
	void onChangeA7( final Map<Integer, String> added ){}

	@OnChange( "component.list" )
	void onChangeA7( final Map<Integer, String> added, final Object removed ){}

	@OnChange( "component.list" )
	void onChangeA7( final Map<Integer, String> added, final Map<Integer, String> removed ){}

	@OnChange( "component.list" )
	void onChangeA7( final Map<Integer, String> added, final Map<Integer, String> removed, final Object updated ){}

	@OnChange( "component.list" )
	void onChangeA7( final Map<Integer, String> added, final Map<Integer, String> removed, final Map<Integer, SList.Update<String>> updated ){}

	@OnChange( "component.list" )
	void onChangeA7( final Object added ){}

	@OnChange( "component.list" )
	void onChangeA7( final Object added, final Object removed ){}

	@OnChange( "component.list" )
	void onChangeA7( final Object added, final Object removed, final Object updated ){}

	@OnChange( "component.map" )
	void onChangeA8(){}

	@OnChange( "component.map" )
	void onChangeA8( final Map<String, String> added ){}

	@OnChange( "component.map" )
	void onChangeA8( final Map<String, String> added, final Map<String, String> removed ){}

	@OnChange( "component.map" )
	void onChangeA8( final Map<String, String> added, final Map<String, String> removed, final Map<String, SMap.Update<String>> updated ){}

	@OnChange( "component.map" )
	void onChangeA8( final Object added ){}

	@OnChange( "component.map" )
	void onChangeA8( final Object added, final Object removed ){}

	@OnChange( "component.map" )
	void onChangeA8( final Object added, final Object removed, final Object updated ){}

	@OnChange( "component.movable_list" )
	void onChangeA9(){}

	@OnChange( "component.movable_list" )
	void onChangeA9( final Map<Integer, String> added ){}

	@OnChange( "component.movable_list" )
	void onChangeA9( final SortedMap<Integer, String> added, final Map<Integer, String> removed ){}

	@OnChange( "component.movable_list" )
	void onChangeA9( final Map<Integer, String> added, final SortedMap<Integer, String> removed, final Map<Integer, Update<String>> updated ){}

	@OnChange( "component.movable_list" )
	void onChangeA9( final SortedMap<Integer, String> added, final SortedMap<Integer, String> removed, final SortedMap<Integer, Update<String>> updated, final List<Move<String>> newMoved, final List<Move<String>> oldMoved ){}

	@OnChange( "component.movable_list" )
	void onChangeA9( final Object added ){}

	@OnChange( "component.movable_list" )
	void onChangeA9( final Object added, final Map<Integer, String> removed ){}

	@OnChange( "component.movable_list" )
	void onChangeA9( final Map<Integer, String> added, final Object removed, final Map<Integer, Update<String>> updated ){}

	@OnChange( "component.movable_list" )
	void onChangeA9( final Object added, final Object removed, final Object updated, final Object moved ){}

	@OnChange( "component.navigable_map" )
	void onChangeA10(){}

	@OnChange( "component.navigable_map" )
	void onChangeA10( final SortedMap<String, String> added ){}

	@OnChange( "component.navigable_map" )
	void onChangeA10( final SortedMap<String, String> added, final SortedMap<String, String> removed ){}

	@OnChange( "component.navigable_map" )
	void onChangeA10( final Object added ){}

	@OnChange( "component.navigable_map" )
	void onChangeA10( final Object added, final Object removed ){}
}
