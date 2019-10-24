/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package shared.callable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SharedCallableApplication {
	public static void main(final String[] args){
		SpringApplication.run(SharedCallableApplication.class, args);
	}
}
