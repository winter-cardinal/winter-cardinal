/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package method_override;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class MethodOverrideSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure( final HttpSecurity http ) throws Exception {
		http.authorizeRequests().antMatchers("/**").permitAll().anyRequest().permitAll();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
		http.csrf().disable();
	}
}
