/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.scope.shared;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedComponentScopeConfig {
	static final SharedComponentScope sharedComponentScope = new SharedComponentScope();

	@Bean
	static CustomScopeConfigurer sharedComponentScopeConfigurer(){
		final CustomScopeConfigurer configurer = new CustomScopeConfigurer ();
		final Map<String, Object> mapping = new HashMap<String, Object>();
		mapping.put("shared-component", sharedComponentScope);
		configurer.setScopes(mapping);
		return configurer;
	}

	@PreDestroy
	void destroy() {
		sharedComponentScope.destroy();
	}
}
