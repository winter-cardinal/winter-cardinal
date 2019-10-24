/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.controller.AbstractPopup;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SLong;

public class BasicsFactoryParameterPopup extends AbstractPopup {
	@Autowired
	SLong parameter0;

	@Autowired
	SLong parameter1;

	@Autowired
	SLong parameter2;

	@OnCreate
	void onCreate(){
		final ArrayNode parameters = getFactoryParameters();
		if( parameters.size() == 1 ) {
			final JsonNode first = parameters.get( 0 );
			if( first.isIntegralNumber() ) {
				this.parameter0.set( first.asLong() );
			}
		}
	}

	@OnCreate
	void onCreateWithParameter( final long parameter ){
		this.parameter1.set( parameter );
	}

	@OnCreate
	void onCreateWithTooManyParameter( final long parameter0, final long parameter1 ){
		this.parameter2.set( parameter0 );
	}
}
