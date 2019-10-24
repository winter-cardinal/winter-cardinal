/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.DisplayName;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnHide;
import org.wcardinal.controller.annotation.OnShow;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.exception.NotReadyException;

@Controller
public class BasicsPageController {
	@Autowired
	@DisplayName( "page-name-1" )
	BasicsPage page;

	//--------------------------------------
	// POST CONSTRUCT
	//--------------------------------------
	AtomicBoolean getDisplayName_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean setDisplayName_PostConstruct_result = new AtomicBoolean( false );
	@PostConstruct
	void onPostConstruct(){
		// getDisplayName
		try {
			page.facade.getDisplayName();
		} catch( final NotReadyException e ){
			getDisplayName_PostConstruct_result.set( true );
		}

		// setDisplayName
		try {
			page.facade.setDisplayName( "--" );
		} catch( final NotReadyException e ){
			setDisplayName_PostConstruct_result.set( true );
		}
	}

	@Callable
	boolean getDisplayName_PostConstruct_check(){
		return getDisplayName_PostConstruct_result.get();
	}

	@Callable
	boolean setDisplayName_PostConstruct_check(){
		return setDisplayName_PostConstruct_result.get();
	}

	//--------------------------------------
	// VISIBILITY
	//--------------------------------------
	@Autowired
	SInteger shown;

	@Autowired
	SInteger hidden;

	@OnCreate
	void onCreate(){
		page.facade.hide();

		shown.set( 0 );
		hidden.set( 0 );
	}

	@OnShow( "page" )
	void onShow(){
		shown.incrementAndGet();
	}

	@OnShow( "page" )
	@Unlocked
	void onShowUnlocked(){
		shown.incrementAndGet();
	}

	@OnHide( "page" )
	void onHide(){
		hidden.incrementAndGet();
	}

	@OnHide( "page" )
	@Unlocked
	void onHideUnlocked(){
		hidden.incrementAndGet();
	}

	@Callable
	boolean show_check(){
		if( page.facade.isShown() ) page.facade.hide();
		if( page.facade.isShown() ) return false;
		page.facade.show();
		if( page.facade.isHidden() ) return false;
		page.facade.hide();
		return page.facade.isHidden();
	}

	//--------------------------------------
	// DISPLAY NAME
	//--------------------------------------
	@Callable
	boolean display_name_check(){
		if( Objects.equal( page.facade.getDisplayName(), "page-name-1" ) ){
			page.facade.setDisplayName( "page-name-2" );
			if( Objects.equal( page.facade.getDisplayName(), "page-name-2" ) ) {
				return true;
			}
		}

		return false;
	}
}
