/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.PageFactory;
import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;

@Controller
public class BasicsFactoryController {
	@Autowired
	PageFactory<BasicsFactoryPage> pages;

	@Autowired
	PopupFactory<BasicsFactoryPopup> popups;

	@Autowired
	ComponentFactory<BasicsFactoryComponent> components;

	@Autowired
	PageFactory<BasicsFactoryPage> spages;

	@Autowired
	PopupFactory<BasicsFactoryPopup> spopups;

	@Autowired
	ComponentFactory<BasicsFactoryComponent> scomponents;

	@OnCreate
	@Callable
	void create(){
		pages.create();
		popups.create();
		components.create();

		spages.create();
		spopups.create();
		scomponents.create();
	}

	@Callable
	boolean checkSize(){
		return
			spages.size() == 1 &&
			spopups.size() == 1 &&
			scomponents.size() == 1;
	}

	@Callable
	boolean checkIsEmpty(){
		return
			spages.isEmpty() == false &&
			spopups.isEmpty() == false &&
			scomponents.isEmpty() == false;
	}

	@Callable
	boolean checkGet(){
		BasicsFactoryPage page = null;
		for( final BasicsFactoryPage v: spages ){
			page = v;
		}

		BasicsFactoryPopup popup = null;
		for( final BasicsFactoryPopup v: spopups ){
			popup = v;
		}

		BasicsFactoryComponent component = null;
		for( final BasicsFactoryComponent v: scomponents ){
			component = v;
		}

		return
			spages.get( 0 ) == page &&
			spopups.get( 0 ) == popup &&
			scomponents.get( 0 ) == component &&
			spages.get( 1 ) == null &&
			spopups.get( 1 ) == null &&
			scomponents.get( 1 ) == null;
	}

	@Callable
	boolean checkContains(){
		return (
			spages.contains( spages.get( 0 ) ) == true &&
			spopups.contains( spopups.get( 0 ) ) == true &&
			scomponents.contains( scomponents.get( 0 ) ) == true &&
			spages.contains( null ) == false &&
			spopups.contains( null ) == false &&
			scomponents.contains( null ) == false
		);
	}

	@Callable
	boolean checkIndexOf(){
		return
			spages.indexOf( spages.get( 0 ) ) == 0 &&
			spopups.indexOf( spopups.get( 0 ) ) == 0 &&
			scomponents.indexOf( scomponents.get( 0 ) ) == 0 &&
			spages.indexOf( null ) == -1 &&
			spopups.indexOf( null ) == -1 &&
			scomponents.indexOf( null ) == -1;
	}

	@Callable
	boolean checkRemove(){
		final BasicsFactoryPopup popup = spopups.get( 0 );

		if( spopups.remove( 1 ) == null ){
			if( spopups.remove( 0 ) == popup ){
				return spopups.size() == 0 && spopups.isEmpty() == true;
			}
		}

		return false;
	}

	@Callable
	boolean checkDestroy(){
		final BasicsFactoryPage page = spages.get( 0 );

		if( spages.destroy( null ) != true ){
			if( spages.destroy( page ) == true ){
				return spages.size() == 0 && spages.isEmpty() == true;
			}
		}

		return false;
	}

	@Callable
	boolean checkClear(){
		scomponents.clear();
		return
			scomponents.size() == 0 &&
			scomponents.isEmpty() == true;
	}

	@Autowired
	ComponentFactory<BasicsFactoryComponent> timings;

	@Callable
	void createTiming(){
		timings.create();
	}
}
