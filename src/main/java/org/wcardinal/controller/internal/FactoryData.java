/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.controller.internal.info.DynamicInfo;
import org.wcardinal.controller.internal.info.SenderIdAndDynamicInfo;

@JsonInclude(Include.NON_NULL)
public class FactoryData {
	public final String name;
	public final long senderId;
	public final DynamicInfo dynamic;
	public final ArrayNode args;

	@JsonIgnore
	public Object[] arguments;

	@JsonCreator
	public FactoryData(
		@JsonProperty( "name" ) final String name,
		@JsonProperty( "senderId" ) final long senderId,
		@JsonProperty( "dynamic" ) final DynamicInfo dynamic,
		@JsonProperty( "args" ) final ArrayNode args ) {
		this( name,  senderId, dynamic, args, null );
	}

	public FactoryData( final String name, final SenderIdAndDynamicInfo senderIdAndDynamicInfo, final ArrayNode args, final Object[] arguments ) {
		this( name, getSenderId( senderIdAndDynamicInfo ), getDynamicInfo( senderIdAndDynamicInfo ), args, arguments );
	}

	static long getSenderId( final SenderIdAndDynamicInfo senderIdAndDynamicInfo ) {
		return ( senderIdAndDynamicInfo != null ? senderIdAndDynamicInfo.senderId : -1 );
	}

	static DynamicInfo getDynamicInfo( final SenderIdAndDynamicInfo senderIdAndDynamicInfo ) {
		return ( senderIdAndDynamicInfo != null ? senderIdAndDynamicInfo.dynamicInfo : null );
	}

	public FactoryData( final String name, final long senderId, final DynamicInfo dynamic, final ArrayNode args, final Object[] arguments ) {
		this.name = name;
		this.senderId = senderId;
		this.dynamic = dynamic;
		this.args = args;
		this.arguments = arguments;
	}
}
