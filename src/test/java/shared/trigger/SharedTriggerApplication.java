/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package shared.trigger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SharedTriggerApplication {
	public static void main(final String[] args){
		SpringApplication.run(SharedTriggerApplication.class, args);
	}
}
