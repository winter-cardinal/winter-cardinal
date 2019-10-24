/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.wcardinal.controller.data.SScalar;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

abstract class SScalarImpl<T> extends SBaseImpl<T> implements SScalar<T> {
	T value;

	public SScalarImpl(final int type ){
		super( type );
		value = null;
	}

	public SScalarImpl(final int type, final T initialValue) {
		super( type );
		value = initialValue;
	}

	@Override
	public T get(){
		lock.lock();
		final T result = value;
		lock.unlock();
		return result;
	}

	@Override
	public T set(final T value){
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, value );
			final T result = this.value;
			this.value = value;
			isChanged = true;
			isInitialized = true;
			return result;
		}
	}

	@Override
	public T reset(){
		try( final Unlocker unlocker = lock() ) {
			final T result = this.value;
			isChanged = true;
			isInitialized = true;
			return result;
		}
	}

	@Override
	public T getValue(){
		return get();
	}

	@Override
	public T setValue( final T value ){
		return set( value );
	}

	@Override
	public T resetValue(){
		return reset();
	}

	@Override
	public void toDirty(){
		lock();
		isChanged = true;
		isInitialized = true;
		unlock();
	}

	boolean setIf( final T expected, final T update ) {
		SCheckers.checkNonNull( isNonNull, update );
		if( Objects.equals(expected, value) != true ) return false;
		value = update;
		isChanged = true;
		isInitialized = true;
		return true;
	}

	@Override
	public boolean compareAndSet(final T expected, final T update){
		try( final Unlocker unlocker = lock() ) {
			return setIf( expected, update );
		}
	}

	@Override
	public T getAndSet( final T value ){
		return set( value );
	}

	@Override
	public Object pack( final SData sdata ) {
		lock.lock();
		final T result = value;
		if( isWeak ) {
			value = null;
			sdata.setAuthorizedRevision( revision );
		}
		lock.unlock();
		return result;
	}

	@Override
	public void onAuthorized( final long authorizedRevision ) {}

	@Override
	public SChange unpack( final JsonNode valueNode, final long revision, final SData sdata ) throws Exception {
		// Cast
		T newValue = null;
		try{
			newValue = cast( valueNode );
		} catch ( final Exception e ){
			logger.error("Failed to cast a value", e);
		}

		// Null check
		if( isNonNull && newValue == null ){
			if( value != null ) {
				override( revision );
			}
			return null;
		}

		// Update
		final T oldValue = value;
		this.value = newValue;
		this.revision = revision;
		this.isInitialized = true;
		sdata.setAuthorizedRevision( revision );
		if( Objects.equals(newValue, oldValue) ) {
			return null;
		} else {
			return new SChangeTwoStates( newValue, oldValue );
		}
	}

	@Override
	public void compact( final long authorizedRevision ) {
		try( final AutoCloseableReentrantLock lock = this.lock.open() ) {
			if( revision <= authorizedRevision ) {
				value = null;
			}
		}
	}

	@Override
	public String toString(){
		return Objects.toString(get());
	}

	@Override
	public boolean equals( final Object value ){
		if( value instanceof SScalar ){
			final SScalar<?> sscalar = (SScalar<?>) value;
			final Object sscalarValue = sscalar.get();
			return Objects.equals(get(), sscalarValue);
		} else {
			return Objects.equals(get(), value);
		}
	}

	@Override
	public void initialize(){
		lock();
		if( isInitialized != true ) {
			isChanged = true;
			isInitialized = true;
		}
		unlock();
	}

	abstract T cast(final JsonNode valueNode) throws Exception;

	@Override
	public void setNonNull(final boolean isNonNull){
		lock.lock();
		this.isNonNull = isNonNull;
		if( isNonNull && value == null ) {
			value = makeNonNullValue();
		}
		lock.unlock();
	}

	abstract T makeNonNullValue();

	@Override
	public int size(){
		final T value = get();
		if( value == null ) return 0;
		if( value instanceof Collection ){
			return ((Collection<?>)value).size();
		} else if( value instanceof Map ){
			return ((Map<?, ?>)value).size();
		} else if( value instanceof ArrayNode ) {
			return ((ArrayNode)value).size();
		} else if( value instanceof ObjectNode ) {
			return ((ObjectNode)value).size();
		} else if( value.getClass().isArray() ) {
			return Array.getLength( value );
		} else {
			return 1;
		}
	}

	@Override
	public boolean isEmpty(){
		return size() <= 0;
	}

	@Override
	public boolean isNull() {
		return get() == null;
	}

	@Override
	public boolean isNotNull() {
		return get() != null;
	}

	@Override
	public int indexOf( final Object o ){
		final T value = get();
		if( value != null ) {
			if( value instanceof List ){
				return ((List<?>)value).indexOf( o );
			} else if( value instanceof ArrayNode ) {
				final ArrayNode node = (ArrayNode)value;
				for( int i=0; i<node.size(); ++i ) {
					if( Objects.equals( node.get( i ), o ) ) return i;
				}
			} else if( value.getClass().isArray() ) {
				final int length = Array.getLength( value );
				for( int i=0; i<length; ++i ) {
					if( Objects.equals( Array.get(value, i), o ) ) return i;
				}
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf( final Object o ){
		final T value = get();
		if( value != null ) {
			if( value instanceof List ){
				return ((List<?>)value).lastIndexOf( o );
			} else if( value instanceof ArrayNode ) {
				final ArrayNode node = (ArrayNode)value;
				for( int i=node.size()-1; 0<=i; --i ) {
					if( Objects.equals( node.get( i ), o ) ) return i;
				}
			} else if( value.getClass().isArray() ) {
				final int length = Array.getLength( value );
				for( int i=length-1; 0<=i; --i ) {
					if( Objects.equals( Array.get(value, i), o ) ) return i;
				}
			}
		}
		return -1;
	}

	@Override
	public boolean contains( final Object o ){
		final T value = get();
		if( value == null ) return false;
		if( value instanceof Collection ){
			return ((Collection<?>)value).contains( o );
		} else if( value instanceof Map ){
			return ((Map<?, ?>)value).containsValue( o );
		} else if( value instanceof ArrayNode ) {
			final ArrayNode node = (ArrayNode)value;
			for( final JsonNode child: node ) {
				if( Objects.equals( child, o ) ) return true;
			}
		} else if( value instanceof ObjectNode ) {
			final ObjectNode node = (ObjectNode)value;
			for( final JsonNode child: node ) {
				if( Objects.equals( child, o ) ) return true;
			}
		} else if( value.getClass().isArray() ) {
			final int length = Array.getLength( value );
			for( int i=length-1; 0<=i; --i ) {
				if( Objects.equals( Array.get(value, i), o ) ) return true;
			}
		} else {
			return Objects.equals(value, o);
		}
		return false;
	}

	@Override
	public boolean containsAll( final Collection<?> others ){
		final T value = get();
		if( value instanceof Collection ){
			return ((Collection<?>)value).containsAll( others );
		} else if( value instanceof Map ){
			final Map<?, ?> map = (Map<?, ?>) value;
			for( final Object o: others ) {
				if( map.containsValue( o ) != true ) return false;
			}
		} else if( value instanceof ArrayNode ) {
			final ArrayNode node = (ArrayNode) value;
			for( final Object o: others ) {
				boolean found = false;
				for( final JsonNode child: node ) {
					if( Objects.equals( child, o ) ) {
						found = true;
						break;
					}
				}
				if( found == false ) return false;
			}
		} else if( value instanceof ObjectNode ) {
			final ObjectNode node = (ObjectNode) value;
			for( final Object o: others ) {
				boolean found = false;
				for( final JsonNode child: node ) {
					if( Objects.equals( child, o ) ) {
						found = true;
						break;
					}
				}
				if( found == false ) return false;
			}
		} else if( value != null && value.getClass().isArray() ) {
			final int length = Array.getLength( value );
			for( final Object o: others ) {
				boolean found = false;
				for( int i=0; i<length; ++i ) {
					final Object element = Array.get(value, i);
					if( Objects.equals( element, o ) ) {
						found = true;
						break;
					}
				}
				if( found == false ) return false;
			}
		} else {
			for( final Object o: others ) {
				if( Objects.equals(value, o) != true ) return false;
			}
		}
		return true;
	}
}
