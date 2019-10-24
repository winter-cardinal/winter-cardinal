/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Objects;

import org.wcardinal.controller.data.SInteger;
import org.wcardinal.util.doc.ThreadSafe;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SIntegerImpl extends SNumberImpl<Integer> implements SInteger {
	public SIntegerImpl() {
		super( SType.INTEGER );
	}

	@Override
	Integer cast(final JsonNode valueElement) throws Exception {
		if( valueElement == null || valueElement.isNumber() != true ) return null;
		return valueElement.asInt();
	}

	@Override
	public Integer addAndGet(final Integer delta) {
		if( delta == null ) throw new NullPointerException();

		try( final Unlocker unlocker = lock() ) {
			if( value !=null ) {
				value += delta;
				isChanged = true;
				isInitialized = true;
			}
			return value;
		}
	}

	Integer add( final int delta ) {
		if( value != null ) {
			final Integer old = value;
			value += delta;
			isChanged = true;
			isInitialized = true;
			return old;
		} else {
			return value;
		}
	}

	@Override
	public Integer getAndAdd(final Integer delta) {
		if( delta == null ) throw new NullPointerException();

		try( final Unlocker unlocker = lock() ) {
			return add( (int) delta );
		}
	}

	@Override
	public Integer decrementAndGet() {
		return addAndGet( -1 );
	}

	@Override
	public Integer getAndDecrement() {
		return getAndAdd( -1 );
	}

	@Override
	public Integer getAndIncrement() {
		return getAndAdd( +1 );
	}

	@Override
	public Integer incrementAndGet() {
		return addAndGet( +1 );
	}

	@Override
	@ThreadSafe
	public int compareTo( final Integer value ){
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( this.value != null ?
				( value != null ? ( this.value < value ? -1 : (this.value > value ? +1 : 0) ) : +1 ) :
				( value != null ? -1 : 0 )
			);
		}
	}

	@Override
	Integer makeNonNullValue() {
		return 0;
	}

	@Override
	public boolean equals( final Number target ) {
		return Objects.equal( value, target );
	}

	@Override
	public boolean equals( final Integer target ) {
		return Objects.equal( value, target );
	}

	@Override
	public boolean equals( final int target ) {
		return Objects.equal( value, target );
	}
}
