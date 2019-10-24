/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Locale;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Objects;

import org.wcardinal.controller.data.SString;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SStringImpl extends SScalarImpl<String> implements SString {
	public SStringImpl() {
		super( SType.STRING.ordinal() );
	}

	public SStringImpl( final String initialValue ) {
		super( SType.STRING.ordinal(), initialValue );
	}

	@Override
	String cast(final JsonNode valueNode) throws Exception {
		if( valueNode == null || valueNode.isNull() || valueNode.isTextual() != true ) return null;
		return valueNode.asText();
	}

	@Override
	public int compareTo( final String string ){
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ?
				( string != null ? value.compareTo(string) : +1 ) :
				( string != null ? -1 : 0 )
			);
		}
	}

	@Override
	public int compareToIgnoreCase( final String string ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ?
				( string != null ? value.compareToIgnoreCase(string) : +1 ) :
				( string != null ? -1 : 0 )
			);
		}
	}

	@Override
	public String concat( final String string ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? ( string != null ? value.concat( string ) : value ) : null );
		}
	}

	@Override
	public boolean contains( final CharSequence sequence ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.contains( sequence ) : false );
		}
	}

	@Override
	public boolean endsWith( final String suffix ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.endsWith( suffix ) : false );
		}
	}

	@Override
	public boolean equalsIgnoreCase( final String string ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.equalsIgnoreCase( string ) : string == null );
		}
	}

	@Override
	public int indexOf( final int ch ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.indexOf( ch ) : -1 );
		}
	}

	@Override
	public int indexOf( final int ch, final int fromIndex ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.indexOf( ch, fromIndex ) : -1 );
		}
	}

	@Override
	public int indexOf( final String substring ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.indexOf( substring ) : -1 );
		}
	}

	@Override
	public int indexOf( final String substring, final int fromIndex ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.indexOf( substring, fromIndex ) : -1 );
		}
	}

	@Override
	public boolean isEmpty() {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.isEmpty() :  true );
		}
	}

	@Override
	public int lastIndexOf( final int ch ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.lastIndexOf( ch ) : -1 );
		}
	}

	@Override
	public int lastIndexOf( final int ch, final int fromIndex ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.lastIndexOf( ch, fromIndex ) : -1 );
		}
	}

	@Override
	public int lastIndexOf( final String substring ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.lastIndexOf( substring ) : -1 );
		}
	}

	@Override
	public int lastIndexOf( final String substring, final int fromIndex ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.lastIndexOf( substring, fromIndex ) : -1 );
		}
	}

	@Override
	public int length() {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.length() : -1 );
		}
	}

	@Override
	public boolean matches( final String regex ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.matches( regex ) : false );
		}
	}

	@Override
	public boolean startsWith( final String prefix ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.startsWith( prefix ) : false );
		}
	}

	@Override
	public boolean startsWith( final String prefix, final int toffset ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.startsWith( prefix, toffset ) : false );
		}
	}

	@Override
	public String substring( final int beginIndex ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			if( value != null ){
				return value.substring( beginIndex );
			} else {
				throw new IndexOutOfBoundsException();
			}
		}
	}

	@Override
	public String substring( final int beginIndex, final int endIndex ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			if( value != null ){
				return value.substring( beginIndex, endIndex );
			} else {
				throw new IndexOutOfBoundsException();
			}
		}
	}

	@Override
	public String toLowerCase() {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.toLowerCase() : null );
		}
	}

	@Override
	public String toLowerCase( final Locale locale ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.toLowerCase( locale ) : null );
		}
	}

	@Override
	public String toUpperCase() {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.toUpperCase() : null );
		}
	}

	@Override
	public String toUpperCase( final Locale locale ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.toUpperCase( locale ) : null );
		}
	}

	@Override
	public String trim() {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( value != null ? value.trim() : null );
		}
	}

	@Override
	String makeNonNullValue() {
		return "";
	}

	@Override
	public boolean equals( final String target ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return Objects.equal( value, target );
		}
	}
}
