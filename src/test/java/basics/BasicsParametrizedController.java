/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableMap;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SBoolean;

@Controller
public class BasicsParametrizedController extends AbstractController {
	@Autowired
	SBoolean get_parameter_result;

	@Autowired
	SBoolean get_parameters_result;

	@Autowired
	SBoolean get_parameter_map_result;

	@OnCreate
	void init(){
		final String foo = getParameter( "foo" );
		final String str = getParameter( "str" );
		final String bar = getParameter( "bar" );
		get_parameter_result.set( "bar".equals(foo) && "123".equals(str) && bar == null );

		final String[] foos = getParameters( "foo" );
		final String[] strs = getParameters( "str" );
		final String[] bars = getParameters( "bar" );
		get_parameters_result.set( foos.length == 1 && "bar".equals(foos[0]) && strs.length == 1 && "123".equals(strs[0]) && bars == null );

		final Map<String, String[]> parameters = getParameterMap();
		get_parameter_map_result.set( Objects.deepEquals(parameters, ImmutableMap.of( "foo", foos, "str", strs )));
	}
}
