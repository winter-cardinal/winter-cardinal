/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package constructor_injection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConstructorInjectionApplication {
	public static void main(final String[] args){
		SpringApplication.run(ConstructorInjectionApplication.class, args);
	}
}
