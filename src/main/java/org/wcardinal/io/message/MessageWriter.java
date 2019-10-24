/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

import java.io.IOException;
import java.io.Writer;

public class MessageWriter extends Writer {
	final int nodeSize;
	final char[] buffer;
	final MessageWriterSender sender;
	int position;

	public MessageWriter( final char[] buffer, final MessageWriterSender sender ) {
		this.nodeSize = buffer.length;
		this.buffer = buffer;
		this.position = 0;
		this.sender = sender;
	}

	public void clear(){

	}

	@Override
	public void flush() throws IOException {

	}

	@Override
	public void close() throws IOException {
		if( 0 < position ) {
			sender.send( new String(buffer, 0, position), true );
			position = 0;
		} else {
			sender.send( "", true );
		}
	}

	@Override
	public void write( final char[] cbuf, int off, int len ) throws IOException {
		while( 0 < len ) {
			if( nodeSize <= position + len ) {
				int rest = nodeSize - position;
				if( Character.isHighSurrogate( cbuf[ off+rest-1 ] ) ) rest -= 1;
				System.arraycopy(cbuf, off, buffer, position, rest);
				sender.send( new String(buffer, 0, position+rest), false );
				position = 0;
				len -= rest;
				off += rest;
			} else {
				System.arraycopy(cbuf, off, buffer, position, len);
				position += len;
				return;
			}
		}
	}
}
