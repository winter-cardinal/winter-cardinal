/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SClass;

enum ENUM_CONSTANT {
	ENUM_CONSTANT_1,
	ENUM_CONSTANT_2
}

class Data {
	public int int_value = 1;
	public double double_value = 1;
	public String string_value = "1";

	public Data( int value ){
		int_value = value;
		double_value = value;
		string_value = String.valueOf( value );
	}
}

@Controller
@Constant( ENUM_CONSTANT.class )
public class BasicsConstantController {
	//-----------------------------------------
	// STATIC CONSTANTS
	//-----------------------------------------
	@Constant
	static int INT_CONSTANT = 1;

	@Constant
	static Data CLASS_CONSTANT = new Data( 1 );

	@Autowired
	SClass<ENUM_CONSTANT> field_enum;

	@Autowired
	SBoolean field_enum_result;

	@OnChange( "field_enum" )
	void onChangeFieldEnum( final ENUM_CONSTANT value ){
		field_enum_result.set( value == ENUM_CONSTANT.ENUM_CONSTANT_2 );
	}

	//-----------------------------------------
	// INSTANCE CONSTANTS
	//-----------------------------------------
	@Constant
	int INT_CONSTANT_INSTANCE;

	@Constant
	Data CLASS_CONSTANT_INSTANCE;

	@OnCreate
	void onCreate(){
		INT_CONSTANT_INSTANCE = 2;
		CLASS_CONSTANT_INSTANCE = new Data( 2 );
	}

	// COMPONENT
	@Autowired
	BasicsConstantComponent component;
}
