/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.maximum_disconnection_time;

import org.springframework.context.annotation.Configuration;

import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.configuration.WCardinalConfigurer;

@Configuration
public class MaximumDisconnectionTimeConfigurer implements WCardinalConfigurer {
	@Override
	public void configure( final WCardinalConfiguration configuration ) {
		configuration.setMaximumDisconnectionTime( 0 );
	}
}
