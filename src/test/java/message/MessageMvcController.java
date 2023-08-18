/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package message;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.wcardinal.util.message.ExposableMessages;

@Controller
public class MessageMvcController {
	@Autowired
	ExposableMessages messages;

	@RequestMapping( "/message/en" )
	ModelAndView en( final HttpServletRequest req ) {
		final ModelAndView mav = new ModelAndView();
		mav.addObject( "messageScript", messages.getScript( Locale.ENGLISH ) );
		mav.addObject( "messageKey1", "a.a" );
		mav.addObject( "messageValue1", "en" );
		mav.addObject( "messageKey2", "b.a" );
		mav.addObject( "messageValue2", "en" );
		mav.addObject( "messageKey3", "c.a" );
		mav.addObject( "messageValue3", "en-a" );
		mav.addObject( "messageKey4", "d.a" );
		mav.addObject( "messageValue4", "en-b-a" );
		mav.addObject( "messageKey5", "e.a" );
		mav.addObject( "messageValue5", "en-a-b" );
		mav.setViewName("message");
		return mav;
	}

	@RequestMapping( "/message/ja" )
	ModelAndView ja( final HttpServletRequest req ) {
		final ModelAndView mav = new ModelAndView();
		mav.addObject( "messageScript", messages.getScript( Locale.JAPANESE ) );
		mav.addObject( "messageKey1", "a.a" );
		mav.addObject( "messageValue1", "ja" );
		mav.addObject( "messageKey2", "b.a" );
		mav.addObject( "messageValue2", "ja" );
		mav.addObject( "messageKey3", "c.a" );
		mav.addObject( "messageValue3", "ja-a" );
		mav.addObject( "messageKey4", "d.a" );
		mav.addObject( "messageValue4", "ja-b-a" );
		mav.addObject( "messageKey5", "e.a" );
		mav.addObject( "messageValue5", "ja-a-b" );
		mav.setViewName("message");
		return mav;
	}

	@RequestMapping( "/message/en/b" )
	ModelAndView enb( final HttpServletRequest req ) {
		final ModelAndView mav = new ModelAndView();
		mav.addObject( "messageScript", messages.getScript( Locale.ENGLISH, "b." ) );
		mav.addObject( "messageKey1", "a.a" );
		mav.addObject( "messageValue1", "a.a" );
		mav.addObject( "messageKey2", "b.a" );
		mav.addObject( "messageValue2", "en" );
		mav.addObject( "messageKey3", "c.a" );
		mav.addObject( "messageValue3", "c.a" );
		mav.addObject( "messageKey4", "d.a" );
		mav.addObject( "messageValue4", "d.a" );
		mav.addObject( "messageKey5", "e.a" );
		mav.addObject( "messageValue5", "e.a" );
		mav.setViewName("message");
		return mav;
	}

	@RequestMapping( "/message/ja/b" )
	ModelAndView jab( final HttpServletRequest req ) {
		final ModelAndView mav = new ModelAndView();
		mav.addObject( "messageScript", messages.getScript( Locale.JAPANESE, "b." ) );
		mav.addObject( "messageKey1", "a.a" );
		mav.addObject( "messageValue1", "a.a" );
		mav.addObject( "messageKey2", "b.a" );
		mav.addObject( "messageValue2", "ja" );
		mav.addObject( "messageKey3", "c.a" );
		mav.addObject( "messageValue3", "c.a" );
		mav.addObject( "messageKey4", "d.a" );
		mav.addObject( "messageValue4", "d.a" );
		mav.addObject( "messageKey5", "e.a" );
		mav.addObject( "messageValue5", "e.a" );
		mav.setViewName("message");
		return mav;
	}
}
