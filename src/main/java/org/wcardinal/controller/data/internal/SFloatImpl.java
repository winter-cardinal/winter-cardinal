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

import org.wcardinal.controller.data.SFloat;
import org.wcardinal.util.doc.ThreadSafe;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SFloatImpl extends SNumberImpl<Float> implements SFloat {
	public SFloatImpl() {
		super( SType.FLOAT );
	}

	@Override
	Float cast(final JsonNode valueElement) throws Exception {
		if( valueElement == null || valueElement.isNumber() != true ) return null;
		return (float) valueElement.asDouble();
	}

	@Override
	public Float addAndGet(final Float delta) {
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

	Float add( final float delta ) {
		if( value != null ) {
			final Float old = value;
			value += delta;
			isChanged = true;
			isInitialized = true;
			return old;
		} else {
			return value;
		}
	}

	@Override
	public Float getAndAdd(final Float delta) {
		if( delta == null ) throw new NullPointerException();

		try( final Unlocker unlocker = lock() ) {
			return add( (float) delta );
		}
	}

	@Override
	public Float decrementAndGet() {
		return addAndGet( -1.0f );
	}

	@Override
	public Float getAndDecrement() {
		return getAndAdd( -1.0f );
	}

	@Override
	public Float getAndIncrement() {
		return getAndAdd( +1.0f );
	}

	@Override
	public Float incrementAndGet() {
		return addAndGet( +1.0f );
	}

	@Override
	@ThreadSafe
	public int compareTo( final Float value ){
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			return ( this.value != null ?
				( value != null ? (this.value < value ? -1 : (this.value > value ? +1 : 0) ) : +1 ) :
				( value != null ? -1 : 0 )
			);
		}
	}

	@Override
	Float makeNonNullValue() {
		return 0.0f;
	}

	@Override
	public boolean equals( final Number target ) {
		return Objects.equal( value, target );
	}

	@Override
	public boolean equals( final Float target ) {
		return Objects.equal( value, target );
	}

	@Override
	public boolean equals( final float target ) {
		return Objects.equal( value, target );
	}
}
