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

import org.wcardinal.controller.data.SDouble;
import org.wcardinal.util.doc.ThreadSafe;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SDoubleImpl extends SNumberImpl<Double> implements SDouble {
	public SDoubleImpl() {
		super( SType.DOUBLE );
	}

	@Override
	Double cast(final JsonNode valueElement) throws Exception {
		if( valueElement == null || valueElement.isNumber() != true ) return null;
		return valueElement.asDouble();
	}

	@Override
	public Double addAndGet(final Double delta) {
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

	Double add( final double delta ) {
		if( value != null ) {
			final Double old = value;
			value += delta;
			isChanged = true;
			isInitialized = true;
			return old;
		} else {
			return value;
		}
	}

	@Override
	public Double getAndAdd(final Double delta) {
		if( delta == null ) throw new NullPointerException();

		try( final Unlocker unlocker = lock() ) {
			return add( (double)delta );
		}
	}

	@Override
	public Double decrementAndGet() {
		return addAndGet( -1.0 );
	}

	@Override
	public Double getAndDecrement() {
		return getAndAdd( -1.0 );
	}

	@Override
	public Double getAndIncrement() {
		return getAndAdd( +1.0 );
	}

	@Override
	public Double incrementAndGet() {
		return addAndGet( +1.0 );
	}

	@Override
	@ThreadSafe
	public int compareTo( final Double value ){
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( this.value != null ?
				( value != null ? (this.value < value ? -1 : (this.value > value ? +1 : 0) ) : +1 ) :
				( value != null ? -1 : 0 )
			);
		}
	}

	@Override
	Double makeNonNullValue() {
		return 0.0;
	}

	@Override
	public boolean equals( final Number target ) {
		return Objects.equal( value, target );
	}

	@Override
	public boolean equals( final Double target ) {
		return Objects.equal( value, target );
	}

	@Override
	public boolean equals( final double target ) {
		return Objects.equal( value, target );
	}
}
