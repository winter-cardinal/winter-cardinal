/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package method_override;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MethodOverrideApplication {
	public static void main(final String[] args){
		SpringApplication.run(MethodOverrideApplication.class, args);
	}
}
