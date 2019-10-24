/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.local_controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalControllerApplication {
	public static void main(final String[] args){
		SpringApplication.run(LocalControllerApplication.class, args);
	}
}
