/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnNotice;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SLong;

@Controller
public class BasicsSharedController {
	@Autowired
	BasicsSharedComponent shared;

	@Autowired
	BasicsUnsharedComponent unshared;

	//---------------------------------------------------
	// CHANGE
	//---------------------------------------------------
	@OnChange( "unshared.name" )
	void onUnsharedChange( final String value ){
		unshared.name.set( value + "B" );
	}

	@OnChange( "shared.name" )
	void onSharedChange( final String value ){
		shared.name.set( value + "B" );
	}

	@OnChange( "shared.name_unlocked" )
	@Unlocked
	void onSharedChangeUnlocked( final String value ){
		shared.name_unlocked.set( value + "B" );
	}

	@OnChange( "unshared.name_unlocked" )
	@Unlocked
	void onUnsharedChangeUnlocked( final String value ){
		unshared.name_unlocked.set( value + "B" );
	}

	//---------------------------------------------------
	// NOTICE
	//---------------------------------------------------
	@Autowired
	SLong shared_notice_count;

	@Autowired
	SLong unshared_notice_count;

	@OnCreate
	void startSharedNotice(){
		shared_notice_count.set( 0L );
		shared.notify("notice");

		unshared_notice_count.set( 0L );
		unshared.notify("notice");
	}

	@OnNotice( "shared.notice" )
	void onSharedNotice(){
		shared_notice_count.incrementAndGet();
	}

	@OnNotice( "shared.notice" )
	@Unlocked
	void onSharedNoticeUnlocked(){
		shared_notice_count.incrementAndGet();
	}

	@OnNotice( "unshared.notice" )
	void onUnsharedNotice(){
		unshared_notice_count.incrementAndGet();
	}

	@OnNotice( "unshared.notice" )
	@Unlocked
	void onUnsharedNoticeUnlocked(){
		unshared_notice_count.incrementAndGet();
	}

	//---------------------------------------------------
	// UNSUPPORTED METHODS
	//---------------------------------------------------
	@Callable
	boolean getName_check(){
		try {
			shared.getName();
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean getAttributes_check(){
		try {
			shared.getAttributes();
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean getLocales_check() {
		try {
			shared.getLocales();
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean getLocale_check() {
		try {
			shared.getLocale();
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean getRemoteAddress_check() {
		try {
			shared.getRemoteAddress();
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean getPrincipal_check() {
		try {
			shared.getPrincipal();
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean getSessionId_check() {
		try {
			shared.getSessionId();
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean getSubSessionId_check() {
		try {
			shared.getSubSessionId();
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}
}
