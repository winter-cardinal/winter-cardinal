/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package encoding.replace;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.wcardinal.controller.ControllerScriptAndSubSession;
import org.wcardinal.controller.Controllers;

@Controller
public class EncodingMvcController {
	@RequestMapping( "/encoding" )
	ModelAndView encoding( final HttpServletRequest req ) {
		final ControllerScriptAndSubSession result = Controllers.get( "/encoding-controller", req );
		final ModelAndView mav = new ModelAndView();
		mav.addObject( "controllerScript", result.script );
		mav.setViewName( "encoding" );
		return mav;
	}
}
