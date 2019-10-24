/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.DisplayNameMessage;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnHide;
import org.wcardinal.controller.annotation.OnShow;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.exception.NotReadyException;

@Controller
public class BasicsPopupController {
	@Autowired
	@DisplayNameMessage( "popup-name-1" )
	BasicsPopup popup;

	//--------------------------------------
	// POST CONSTRUCT
	//--------------------------------------
	AtomicBoolean getDisplayName_PostConstruct_result = new AtomicBoolean( false );
	AtomicBoolean setDisplayName_PostConstruct_result = new AtomicBoolean( false );
	@PostConstruct
	void onPostConstruct(){
		// getDisplayName
		try {
			popup.facade.getDisplayName();
		} catch( final NotReadyException e ){
			getDisplayName_PostConstruct_result.set( true );
		}

		// setDisplayName
		try {
			popup.facade.setDisplayName( "--" );
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
		shown.set( 0 );
		hidden.set( 0 );
	}

	@OnShow( "popup" )
	void onShow(){
		shown.incrementAndGet();
	}

	@OnHide( "popup" )
	void onHide(){
		hidden.incrementAndGet();
	}

	@Callable
	boolean show_check(){
		if( popup.facade.isShown() ) popup.facade.hide();
		if( popup.facade.isShown() ) return false;
		popup.facade.show();
		if( popup.facade.isHidden() ) return false;
		popup.facade.hide();
		return popup.facade.isHidden();
	}

	//--------------------------------------
	// DISPLAY NAME
	//--------------------------------------
	@Callable
	boolean display_name_check(){
		if( Objects.equals( popup.facade.getDisplayName(), "popup-name-1" ) ){
			popup.facade.setDisplayName( "popup-name-2" );
			if( Objects.equals( popup.facade.getDisplayName(), "popup-name-2" ) ) {
				return true;
			}
		}

		return false;
	}
}
