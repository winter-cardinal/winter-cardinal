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

import org.wcardinal.controller.data.SLong;
import org.wcardinal.util.doc.ThreadSafe;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SLongImpl extends SNumberImpl<Long> implements SLong {
	public SLongImpl() {
		super( SType.LONG );
	}

	@Override
	Long cast(final JsonNode valueElement) throws Exception {
		if( valueElement == null || valueElement.isNumber() != true ) return null;
		return valueElement.asLong();
	}

	@Override
	public Long addAndGet(final Long delta) {
		if( delta == null ) throw new NullPointerException();

		try( final Unlocker unlocker = lock() ) {
			if( value != null ) {
				value += delta;
				isChanged = true;
				isInitialized = true;
			}
			return value;
		}
	}

	Long add( final long delta ) {
		if( value != null ) {
			final Long old = value;
			value += delta;
			isChanged = true;
			isInitialized = true;
			return old;
		} else {
			return value;
		}
	}

	@Override
	public Long getAndAdd(final Long delta) {
		if( delta == null ) throw new NullPointerException();

		try( final Unlocker unlocker = lock() ) {
			return add( (long) delta );
		}
	}

	@Override
	public Long decrementAndGet() {
		return addAndGet( -1L );
	}

	@Override
	public Long getAndDecrement() {
		return getAndAdd( -1L );
	}

	@Override
	public Long getAndIncrement() {
		return getAndAdd( +1L );
	}

	@Override
	public Long incrementAndGet() {
		return addAndGet( +1L );
	}

	@Override
	@ThreadSafe
	public int compareTo( final Long value ){
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( this.value != null ?
				( value != null ? (this.value < value ? -1 : (this.value > value ? +1 : 0) ) : +1 ) :
				( value != null ? -1 : 0 )
			);
		}
	}

	@Override
	Long makeNonNullValue() {
		return 0L;
	}

	@Override
	public boolean equals( final Number target ) {
		return Objects.equal( value, target );
	}

	@Override
	public boolean equals( final Long target ) {
		return Objects.equal( value, target );
	}

	@Override
	public boolean equals( final long target ) {
		return Objects.equal( value, target );
	}
}
