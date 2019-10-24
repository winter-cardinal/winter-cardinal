/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package callable.disconnect;

import org.springframework.context.annotation.Configuration;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.configuration.WCardinalConfigurer;

@Configuration
public class CallableDisconnectConfigurer implements WCardinalConfigurer {
	@Override
	public void configure( final WCardinalConfiguration configuration ) {
		// configuration.setMaximumIdleTime( 5000 );
	}
}
