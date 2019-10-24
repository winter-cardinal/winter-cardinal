/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wcardinal.io.message.MessageWriter;
import org.wcardinal.io.message.MessageWriterSender;
import org.wcardinal.io.message.RequestMessage;
import org.wcardinal.io.message.RequestMessages;

public class EndpointRequestMessageWriter {
	static final Logger logger = LoggerFactory.getLogger(EndpointRequestMessageWriter.class);
	static ThreadLocal<SoftReference<char[]>> bufferLocal = new ThreadLocal<>();

	final Endpoint endpoint;
	final Collection<RequestMessage> messages;
	final boolean clearAfterBuild;

	public EndpointRequestMessageWriter( final Endpoint endpoint, final Collection<RequestMessage> messages, final boolean clearAfterBuild ) {
		this.endpoint = endpoint;
		this.messages = messages;
		this.clearAfterBuild = clearAfterBuild;
	}

	boolean write( final int partialMessageSize, final MessageWriterSender sender ) {
		try {
			char[] buffer = null;
			final SoftReference<char[]> writerReference = bufferLocal.get();
			if( writerReference == null ) {
				buffer = new char[ partialMessageSize ];
				bufferLocal.set(new SoftReference<char[]>( buffer ));
			} else {
				buffer = writerReference.get();
				if( buffer == null ) {
					buffer = new char[ partialMessageSize ];
					bufferLocal.set(new SoftReference<char[]>( buffer ));
				}
			}
			return write( buffer, sender );
		} catch( final Exception e ){
			logger.error( e.getMessage(), e );
		}
		return false;
	}

	boolean write( final char[] buffer, final MessageWriterSender sender ) throws IOException {
		final RequestMessage message;
		synchronized( endpoint ) {
			if( messages.size() <= 0 ) return false;
			message = RequestMessages.merge( messages );
			if( clearAfterBuild ){
				messages.clear();
			}
		}

		try {
			final MessageWriter writer = new MessageWriter( buffer, sender );
			message.write( writer );
			writer.close();
		} catch( final Exception e ) {
			return false;
		}
		return true;
	}
}
