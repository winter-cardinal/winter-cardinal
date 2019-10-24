/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package encoding.base64;

import org.springframework.context.annotation.Configuration;

import org.wcardinal.configuration.ControllerVariableEncoding;
import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.configuration.WCardinalConfigurer;

@Configuration
public class EncodingWCardinalConfigurer implements WCardinalConfigurer {
	@Override
	public void configure( final WCardinalConfiguration configuration ) {
		configuration.setEmbeddedControllerVariableEncoding( ControllerVariableEncoding.BASE64 );
	}
}
