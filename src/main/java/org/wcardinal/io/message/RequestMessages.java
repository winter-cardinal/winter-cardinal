/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

import java.util.Collection;

import org.wcardinal.controller.internal.info.AckInfo;

public class RequestMessages {
	private RequestMessages(){}

	public static RequestMessage merge( final Collection<RequestMessage> messages ) {
		if( messages.size() == 1 ){
			for( final RequestMessage message: messages ) {
				return message;
			}
		}

		for( final RequestMessage message: messages ) {
			switch( message.getType() ){
			case "c":
				return message;
			}
		}

		boolean isFirstAck = true;
		AckInfo ack = null;
		MessageArgument argument = null;
		for( final RequestMessage message: messages ){
			if( argument == null ) {
				argument = message.getArgument();
			}

			final AckInfo otherAck = message.getAck();
			if( otherAck != null ) {
				if( ack == null ) {
					ack = otherAck;
				} else if( isFirstAck ) {
					ack = new AckInfo( ack );
					ack.merge( otherAck );
					isFirstAck = false;
				} else {
					ack.merge( otherAck );
				}
			}
		}

		if( argument == null ) {
			return new RequestMessageAck( ack );
		} else {
			if( ack == null ) {
				return new RequestMessageUpdate( argument );
			} else {
				return new RequestMessageUpdateAndAck( ack, argument );
			}
		}
	}
}
