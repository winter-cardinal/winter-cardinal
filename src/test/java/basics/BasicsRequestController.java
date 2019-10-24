/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.ControllerAttributes;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCheck;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnRequest;
import org.wcardinal.controller.data.SInteger;

@Controller
public class BasicsRequestController extends AbstractController {
	static AtomicInteger count = new AtomicInteger( 0 );

	@OnCheck
	static boolean onCheck(){
		count.set( 0 );
		return true;
	}

	@OnRequest
	static void onRequest1( final ControllerAttributes attributes, final HttpServletRequest request ){
		if( request != null ) {
			final Object value = attributes.get("value");
			if( value instanceof Number ) {
				attributes.put("value", ((Number)value).intValue()+1);
			} else {
				attributes.put("value", 1);
			}

			count.incrementAndGet();
		}
	}

	@OnRequest
	static void onRequest2( final ControllerAttributes attributes ){
		final Object value = attributes.get("value");
		if( value instanceof Number ) {
			attributes.put("value", ((Number)value).intValue()+1);
		} else {
			attributes.put("value", 1);
		}

		count.incrementAndGet();
	}

	@OnRequest
	static void onRequest3( final HttpServletRequest request ){
		if( request != null ) {
			count.incrementAndGet();
		}
	}

	@OnRequest
	static void onRequest4(){
		count.incrementAndGet();
	}

	@Autowired
	SInteger integer1;

	@Autowired
	SInteger integer2;

	@OnCreate
	void onCreate(){
		final Object value = this.getAttributes().get("value");
		if( value instanceof Number ){
			integer1.set( ((Number) value).intValue() );
		} else {
			integer1.set( -1 );
		}

		integer2.set(count.get());
	}
}
