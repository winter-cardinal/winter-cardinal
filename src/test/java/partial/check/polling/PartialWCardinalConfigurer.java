/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package partial.check.polling;

import org.springframework.context.annotation.Configuration;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.configuration.WCardinalConfigurer;

@Configuration
public class PartialWCardinalConfigurer implements WCardinalConfigurer {
	@Override
	public void configure( final WCardinalConfiguration configuration ) {
		configuration.setPartialMessageSize( 50 );
		configuration.setDefaultProtocols( "polling-100" );
	}
}
