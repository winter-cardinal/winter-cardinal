/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCheck;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SString;

@Controller(name="BasicsCheckController")
public class BasicsCheckControllerA {
	@OnCheck
	static boolean onCheck( final HttpServletRequest request ){
		return Objects.equal("A", request.getParameter( "type" ));
	}

	@Autowired
	SString name;

	@OnCreate
	void onCreate(){
		name.set("A");
	}
}
