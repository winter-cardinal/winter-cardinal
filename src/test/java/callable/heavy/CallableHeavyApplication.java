/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package callable.heavy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CallableHeavyApplication {
	public static void main(final String[] args){
		SpringApplication.run(CallableHeavyApplication.class, args);
	}
}
