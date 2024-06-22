/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.polling;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.AbstractController;

import org.wcardinal.configuration.WCardinalConfigurationSupport;

@Configuration
@Import(WCardinalConfigurationSupport.class)
@ConditionalOnWebApplication
public class PollingConfig {
	@Autowired
	WCardinalConfigurationSupport configuration;

	@Bean
	public HandlerMapping pollingConfigHandlerMapping(){
		final AbstractController endpoint;
		if( configuration.isSharedConnectionEnabled() ){
			endpoint = new SharedPollingEndpoint( configuration );
		} else{
			endpoint = new PollingEndpoint( configuration );
		}

		final HashMap<String, AbstractController> mapping = new HashMap<>();
		mapping.put(configuration.getPollingPath(), endpoint );

		final SimpleUrlHandlerMapping handler = new SimpleUrlHandlerMapping();
		handler.setOrder( Integer.MAX_VALUE - 2 );
		handler.setUrlMap( mapping );
		handler.setPatternParser(null);
		return handler;
	}
}
