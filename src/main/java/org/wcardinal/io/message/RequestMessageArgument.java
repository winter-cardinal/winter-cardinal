/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

import java.io.IOException;
import java.io.Writer;

import org.wcardinal.controller.internal.RootControllerLockResult;
import org.wcardinal.controller.internal.info.AckInfo;
import org.wcardinal.util.json.Json;

public class RequestMessageArgument extends RequestMessage {
	final MessageArgument argument;

	RequestMessageArgument( final String type, final MessageArgument argument ) {
		super( type );
		this.argument = argument;
	}

	@Override
	public AckInfo getAck() {
		return null;
	}

	@Override
	public MessageArgument getArgument() {
		return argument;
	}

	@Override
	public void write( final Writer writer ) throws IOException {
		final RootControllerLockResult lockResult = argument.lock();
		if( lockResult != null ) {
			try {
				writer.write( type + String.valueOf( lockResult.senderIdAndDynamicInfo.senderId ) + type );
				Json.non_closing_writer.writeValue(writer, lockResult.senderIdAndDynamicInfo.dynamicInfo );
			} finally {
				lockResult.lock.unlock();
			}
		} else {
			writer.write( type.equals( "c" ) ? "d" : "e" );
		}
	}
}
