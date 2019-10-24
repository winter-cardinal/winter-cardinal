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

public class RequestMessageUpdateAndAck extends RequestMessage {
	final AckInfo ack;
	final MessageArgument argument;

	public RequestMessageUpdateAndAck( final AckInfo ack, final MessageArgument argument ) {
		super( "x" );
		this.ack = ack;
		this.argument = argument;
	}

	@Override
	public AckInfo getAck() {
		return ack;
	}

	@Override
	public MessageArgument getArgument() {
		return argument;
	}

	@Override
	public void write( final Writer writer ) throws IOException {
		// UPDATE
		final RootControllerLockResult lockResult = argument.lock();
		if( lockResult != null ) {
			try {
				writer.write( type + String.valueOf(lockResult.senderIdAndDynamicInfo.senderId) + type + "[" );
				Json.non_closing_writer.writeValue(writer, ack);
				writer.write( "," );
				Json.non_closing_writer.writeValue(writer, lockResult.senderIdAndDynamicInfo.dynamicInfo );
				writer.write( "]" );
			} finally {
				lockResult.lock.unlock();
			}
		} else {
			writer.write( "a" );
			Json.non_closing_writer.writeValue(writer, ack);
		}
	}
}
