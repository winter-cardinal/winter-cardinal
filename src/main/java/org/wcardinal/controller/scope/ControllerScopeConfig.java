/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.scope;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerScopeConfig {
	@Bean
	static CustomScopeConfigurer controllerScopeConfigurer(){
		final CustomScopeConfigurer configurer = new CustomScopeConfigurer ();
		final Map<String, Object> mapping = new HashMap<String, Object>();
		mapping.put("controller", new ControllerScope());
		configurer.setScopes(mapping);
		return configurer;
	}
}
