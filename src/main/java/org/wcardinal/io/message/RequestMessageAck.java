/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

import java.io.IOException;
import java.io.Writer;

import org.wcardinal.controller.internal.info.AckInfo;
import org.wcardinal.util.json.Json;

public class RequestMessageAck extends RequestMessage {
	final AckInfo ack;

	public RequestMessageAck( final AckInfo ack ) {
		this( "a", ack );
	}

	protected RequestMessageAck( final String type, final AckInfo ack ) {
		super( type );
		this.ack = ack;
	}

	@Override
	public AckInfo getAck() {
		return ack;
	}

	@Override
	public MessageArgument getArgument() {
		return null;
	}

	@Override
	public void write( final Writer writer ) throws IOException {
		writer.write( type );
		Json.non_closing_writer.writeValue(writer, ack);
	}
}
