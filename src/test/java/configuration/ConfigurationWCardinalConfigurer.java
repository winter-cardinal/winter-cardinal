/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package configuration;

import org.junit.Assert;
import org.springframework.context.annotation.Configuration;

import org.wcardinal.configuration.ControllerVariableEncoding;
import org.wcardinal.configuration.WCardinalConfiguration;
import org.wcardinal.configuration.WCardinalConfigurer;

@Configuration
public class ConfigurationWCardinalConfigurer implements WCardinalConfigurer {
	@Override
	public void configure( final WCardinalConfiguration configuration ) {
		Assert.assertEquals( configuration, configuration.setMaximumBinaryMessageSize( 0 ) );
		Assert.assertEquals( 0, configuration.getMaximumBinaryMessageSize() );

		Assert.assertEquals( configuration, configuration.setMaximumTextMessageSize( 0 ) );
		Assert.assertEquals( 0, configuration.getMaximumTextMessageSize() );

		Assert.assertEquals( configuration, configuration.setMaximumIdleTime( 0 ) );
		Assert.assertEquals( 0, configuration.getMaximumIdleTime() );

		Assert.assertEquals( configuration, configuration.setWebSocketPath( "" ) );
		Assert.assertEquals( "", configuration.getWebSocketPath(), "" );

		Assert.assertEquals( configuration, configuration.setAllowedOrigins( "a", "b" ) );
		Assert.assertArrayEquals( new String[]{"a", "b"}, configuration.getAllowedOrigins() );

		Assert.assertEquals( false, configuration.isControllerHttpEnabled() );
		Assert.assertEquals( configuration, configuration.setControllerHttpEnabled( true ) );
		Assert.assertEquals( true, configuration.isControllerHttpEnabled() );

		Assert.assertEquals( configuration, configuration.setControllerVariableEmbeddable( false ) );
		Assert.assertEquals( false, configuration.isControllerVariableEmbeddable() );

		Assert.assertEquals( configuration, configuration.setEmbeddedControllerVariableEncoding( ControllerVariableEncoding.REPLACE ) );
		Assert.assertEquals( ControllerVariableEncoding.REPLACE, configuration.getEmbeddedControllerVariableEncoding() );

		Assert.assertEquals( configuration, configuration.setPollingPath( "" ) );
		Assert.assertEquals( "", configuration.getPollingPath() );

		Assert.assertEquals( configuration, configuration.setPollingTimeout( 0 ) );
		Assert.assertEquals( 0, configuration.getPollingTimeout() );

		Assert.assertEquals( 2, configuration.getThreadPoolSize() );
		Assert.assertEquals( configuration, configuration.setThreadPoolSize( 1 ) );
		Assert.assertEquals( 1, configuration.getThreadPoolSize() );

		Assert.assertEquals( configuration, configuration.setMessagePoolSize( 1 ) );
		Assert.assertEquals( 1, configuration.getMessagePoolSize() );

		Assert.assertEquals( configuration, configuration.setPartialMessageEnabled( false ) );
		Assert.assertEquals( false, configuration.isPartialMessageEnabled() );

		Assert.assertEquals( configuration, configuration.setPartialMessageSize( 5 ) );
		Assert.assertEquals( 5, configuration.getPartialMessageSize() );

		Assert.assertEquals( configuration, configuration.setSharedConnectionEnabled( true ) );
		Assert.assertEquals( true, configuration.isSharedConnectionEnabled() );

		Assert.assertEquals( configuration, configuration.setDefaultProtocols( "a", "b" ) );
		Assert.assertArrayEquals( new String[]{ "a", "b" }, configuration.getDefaultProtocols() );

		Assert.assertEquals( configuration, configuration.setSyncConnectTimeout( 0 ) );
		Assert.assertEquals( 0, configuration.getSyncConnectTimeout() );

		Assert.assertEquals( configuration, configuration.setSyncUpdateTimeout( 0 ) );
		Assert.assertEquals( 0, configuration.getSyncUpdateTimeout() );

		Assert.assertEquals( configuration, configuration.setSyncUpdateInterval( 0 ) );
		Assert.assertEquals( 0, configuration.getSyncUpdateInterval() );

		Assert.assertEquals( configuration, configuration.setSyncClientConnectTimeout( 0 ) );
		Assert.assertEquals( 0, configuration.getSyncClientConnectTimeout() );

		Assert.assertEquals( configuration, configuration.setSyncClientUpdateTimeout( 0 ) );
		Assert.assertEquals( 0, configuration.getSyncClientUpdateTimeout() );

		Assert.assertEquals( configuration, configuration.setSyncClientUpdateInterval( 0 ) );
		Assert.assertEquals( 0, configuration.getSyncClientUpdateInterval() );
	}
}
