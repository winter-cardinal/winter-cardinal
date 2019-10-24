/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package message;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wcardinal.util.message.ExposableReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {
	@Bean
	public MessageSource messageSource() {
		final ExposableReloadableResourceBundleMessageSource result = new ExposableReloadableResourceBundleMessageSource();
		result.setBasename("classpath:/message/i18n/messages");
		result.setDefaultEncoding("UTF-8");
		return result;
	}
}
