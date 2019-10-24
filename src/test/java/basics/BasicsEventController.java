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
import org.wcardinal.controller.data.SInteger;

@Controller
public class BasicsEventController {
	@Autowired
	SInteger field_integer;

	@OnChange( "field_integer" )
	void update( final Integer value ){
		if( 1 <= value ) {
			if( value != 2 ) {
				this.field_integer.set( 2 );
			}
		} else {
			this.field_integer.set( value+1 );
		}
	}

	@Autowired
	SInteger field_1;

	@Autowired
	SInteger field_2;

	@Autowired
	SInteger field_3;

	@OnCreate
	void onCreate(){
		field_3.set( 0 );
	}

	@OnChange( "field_1" )
	void onField1Change(){
		if( field_1.equals( 1 ) && field_2.equals( 1 ) ){
			field_3.incrementAndGet();
		}
	}

	@OnChange( "field_2" )
	void onField2Change(){
		if( field_1.equals( 1 ) && field_2.equals( 1 ) ){
			field_3.incrementAndGet();
		}
	}

	@Callable
	void start(){
		field_1.lock();
		try {
			field_1.set( 2 );
			field_2.set( 2 );
		} finally {
			field_1.unlock();
		}
	}
}
