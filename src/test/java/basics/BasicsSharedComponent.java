/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.List;

import org.jdeferred.DoneCallback;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.controller.AbstractComponent;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnNotice;
import org.wcardinal.controller.annotation.SharedComponent;
import org.wcardinal.controller.annotation.Unlocked;
import org.wcardinal.controller.data.SLong;
import org.wcardinal.controller.data.SString;
import org.wcardinal.util.thread.Unlocker;

@SharedComponent
public class BasicsSharedComponent extends AbstractComponent {
	@Autowired
	SString name;

	@Autowired
	SString name_unlocked;

	@Callable
	String hello( final String name ){
		return "Hello, "+name;
	}

	@Callable
	void foo(){
		this.triggerAndWait("foo", 3000, "A").then(new DoneCallback<List<JsonNode>>(){
			@Override
			public void onDone(final List<JsonNode> result) {
				trigger( "bar", result.get( 0 ).asText() + "B" );
			}
		});
	}

	//---------------------------------------------------
	// CHANGE
	//---------------------------------------------------
	@Autowired
	SString name_direct;

	@Autowired
	SString name_direct_unlocked;

	@OnChange( "name_direct" )
	void onChange( final String value ){
		name_direct.set( value + "B" );
	}

	@OnChange( "name_direct_unlocked" )
	@Unlocked
	void onChangeUnlocked( final String value ){
		try ( final Unlocker unlocker = lock() ) {
			name_direct_unlocked.set( value + "B" );
		}
	}

	//---------------------------------------------------
	// NOTICE
	//---------------------------------------------------
	@Autowired
	SLong notice_count;

	@OnCreate
	void onCreate(){
		notice_count.set( 0L );
	}

	@OnNotice( "notice" )
	void onNotice(){
		notice_count.incrementAndGet();
	}

	@OnNotice( "notice" )
	@Unlocked
	void onNoticeUnlocked(){
		notice_count.incrementAndGet();
	}
}
