/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConfigurationApplication {
	public static void main(final String[] args){
		SpringApplication.run(ConfigurationApplication.class, ConfigurationTest.getArgs());
	}
}
