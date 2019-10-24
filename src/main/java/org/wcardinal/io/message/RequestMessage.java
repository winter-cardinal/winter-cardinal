/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

import java.io.IOException;
import java.io.Writer;

import org.wcardinal.controller.internal.info.AckInfo;

public abstract class RequestMessage {
	final String type;

	public RequestMessage( final String type ){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	abstract public AckInfo getAck();
	abstract public MessageArgument getArgument();
	abstract public void write( final Writer staticWriter ) throws IOException;

	@Override
	public String toString(){
		return "RequestMessage( type:"+type+" )";
	}
}
