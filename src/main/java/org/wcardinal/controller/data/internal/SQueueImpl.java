/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import org.wcardinal.controller.data.SQueue;
import org.wcardinal.controller.internal.Controller;
import org.wcardinal.controller.internal.ControllerDynamicInfoHandler;
import org.wcardinal.controller.internal.Property;
import org.wcardinal.controller.internal.info.DynamicDataObject;
import org.wcardinal.controller.internal.info.SetDynamicDataMap;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SQueueImpl<V> extends SQueue<V> implements SQueueContainer<V>, ControllerDynamicInfoHandler {
	String name = null;
	Controller controller = null;
	boolean isReadOnly = false;
	boolean isNonNull = false;
	boolean isSoft = false;
	boolean isInitialized = false;
	AutoCloseableReentrantLock lock;
	ResolvableType type;

	long revision = 0;
	final SQueueValues<V> values = new SQueueValues<V>();
	final Map<Object, SQueueDataImpl<V>> originToData = new HashMap<>();

	public SQueueImpl() {}

	@Override
	public void init( final String name, final Controller controller, final AutoCloseableReentrantLock lock, final ResolvableType type, final EnumSet<Property> properties ) {
		this.name = name;
		this.controller = controller;
		this.lock = lock;
		this.type = type;
		this.isReadOnly = properties.contains( Property.READ_ONLY );
		this.isNonNull = properties.contains( Property.NON_NULL );
		this.isSoft = properties.contains( Property.SOFT );
		this.isInitialized = ! properties.contains( Property.UNINITIALIZED );

		controller.put( this );
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getRevision() {
		return revision;
	}

	@Override
	public void setRevision( final long revision ) {
		this.revision = revision;
	}

	@Override
	public SQueueValues<V> getValue() {
		return values;
	}

	@Override
	public Controller getController() {
		return controller;
	}

	@Override
	public AutoCloseableReentrantLock getLock() {
		return lock;
	}

	@Override
	public ResolvableType getType() {
		return type;
	}

	@Override
	public void addOrigin( final Object origin ) {
		if( originToData.containsKey( origin ) != true ) {
			originToData.put( origin, new SQueueDataImpl<V>( this, origin ) );
		}
	}

	@Override
	public void removeOrigin( final Object origin ) {
		originToData.remove( origin );
	}

	@Override
	public void handle( final Object origin, final SetDynamicDataMap map ) {
		final SQueueDataImpl<V> data = originToData.get( origin );
		if( data != null ) data.handle( map );
	}

	@Override
	public void handle( final Object origin, final Map<String, SData> nameToSData, final Map<String, DynamicDataObject> nameToData, final long senderId ) {
		final SQueueDataImpl<V> data = originToData.get( origin );
		if( data != null ) data.handle( origin, nameToSData, nameToData, senderId );
	}

	@Override
	public boolean isSoft() {
		return isSoft;
	}

	@Override
	public boolean compact() {
		if( isSoft != true || controller == null || controller.isShared() || values.isEmpty() ) return false;
		this.values.clear();
		return true;
	}

	// OPERATIONS
	@Override
	public boolean onAdd( final V value ) {
		for( final SQueueDataImpl<V> data: originToData.values() ) {
			data.onAdd( value );
		}
		return true;
	}

	@Override
	public boolean onAddAll( final Collection<? extends V> values ) {
		if( values.isEmpty() != true ) {
			for( final SQueueDataImpl<V> data: originToData.values() ) {
				data.onAddAll( values );
			}
			return true;
		}
		return false;
	}

	@Override
	public void onRemove() {
		for( final SQueueDataImpl<V> data: originToData.values() ) {
			data.onRemove();
		}
	}

	@Override
	public void onClear() {
		for( final SQueueDataImpl<V> data: originToData.values() ) {
			data.onClear();
		}
	}

	@Override
	public void onCapacity( final int capacity ) {
		for( final SQueueDataImpl<V> data: originToData.values() ) {
			data.onCapacity( capacity );
		}
	}

	@Override
	public void onChange() {
		revision += 1;
		isInitialized = true;
	}

	@Override
	public void onInitialize( final Object except ) {
		for( final SQueueDataImpl<V> data: originToData.values() ) {
			if( data == except ) continue;
			data.onInitialize();
		}
	}

	@Override
	public void onPatches( final Object except, final SPatchesPacked<SQueueValues<V>, SQueuePatch<V>> patches ) {
		for( final SQueueDataImpl<V> data: originToData.values() ) {
			if( data == except ) continue;
			data.onPatches( patches );
		}
	}

	void trimToCapacity() {
		while( values.getCapacity() < values.size() ) {
			values.remove();
			onRemove();
		}
	}

	@Override
	public boolean add( final V element ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, element );
			values.add( element );
			onAdd( element );
			trimToCapacity();
			onChange();
			return true;
		}
	}

	@Override
	public boolean addAll( final Collection<? extends V> elements ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, elements );
			values.addAll( elements );
			final boolean result = onAddAll( elements );
			if( result ) {
				trimToCapacity();
				onChange();
			}
			return result;
		}
	}

	@Override
	public void clear() {
		try( final Unlocker unlocker = lock() ) {
			if( values.isEmpty() != true ) {
				values.clear();
				onClear();
				onChange();
			}
		}
	}

	@Override
	public boolean contains( final Object element ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.contains( element );
		}
	}

	@Override
	public boolean containsAll( final Collection<?> elements ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.containsAll( elements );
		}
	}

	@Override
	public boolean isEmpty() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.isEmpty();
		}
	}

	@Override
	public Iterator<V> iterator() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SQueueIterator<V>( this, this, values );
		}
	}

	@Override
	public boolean remove( final Object target ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll( final Collection<?> targets ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll( final Collection<?> targets ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.size();
		}
	}

	@Override
	public Object[] toArray() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.toArray();
		}
	}

	@Override
	public <U> U[] toArray(final U[] array) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.toArray( array );
		}
	}

	@Override
	public boolean isReadOnly(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return isReadOnly;
		}
	}

	@Override
	public boolean isNonNull(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return isNonNull;
		}
	}

	@Override
	public boolean isInitialized(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return isInitialized;
		}
	}

	@Override
	public void initialize(){
		try( final Unlocker unlocker = lock() ) {
			if( isInitialized != true ) {
				isInitialized = true;
				onInitialize( null );
			}
		}
	}

	@Override
	public void initialize( final Object except ) {
		if( isInitialized != true ) {
			isInitialized = true;
			onInitialize( except );
		}
	}

	@Override
	public Unlocker lock(){
		return controller.lock();
	}

	@Override
	public boolean tryLock(){
		return controller.tryLock();
	}

	@Override
	public boolean tryLock(final long timeout, final TimeUnit unit){
		return controller.tryLock(timeout, unit);
	}

	@Override
	public boolean isLocked(){
		return controller.isLocked();
	}

	@Override
	public void unlock() {
		controller.unlock();
	}

	@Override
	public void unlock( final Object origin ) {
		unlock();
	}

	@Override
	public String toString(){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.toString();
		}
	}

	@Override
	public boolean equals( final Object other ){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.equals( other );
		}
	}

	@Override
	public boolean equals( final Collection<? extends V> other ){
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.equals( other );
		}
	}

	@Override
	public boolean clearAndAdd( final V element ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, element );
			clear();
			values.add( element );
			onAdd( element );
			trimToCapacity();
			onChange();
			return true;
		}
	}

	@Override
	public boolean clearAndAddAll( final Collection<? extends V> elements ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, elements );

			boolean result = false;

			// Clear
			if( values.isEmpty() != true ) {
				result = true;
				values.clear();
				onClear();
			}

			// Add all
			values.addAll( elements );
			if( onAddAll( elements ) ) {
				result = true;
			}

			// OnChange
			if( result ) {
				trimToCapacity();
				onChange();
			}

			return result;
		}
	}

	@Override
	public V element() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.element();
		}
	}

	@Override
	public boolean offer( final V element ) {
		return this.add( element );
	}

	@Override
	public boolean clearAndOffer( final V value ) {
		return this.clearAndAdd( value );
	}

	@Override
	public boolean clearAndOfferAll( final Collection<? extends V> values ) {
		return this.clearAndAddAll( values );
	}

	@Override
	public V peek() {
		try( final Unlocker unlocker = lock() ) {
			return values.peek();
		}
	}

	@Override
	public V poll() {
		try( final Unlocker unlocker = lock() ) {
			V result = null;
			if( values.isEmpty() != true ){
				result = values.poll();
				onRemove();
				onChange();
			}
			return result;
		}
	}

	@Override
	public V remove() {
		try( final Unlocker unlocker = lock() ) {
			final V result = values.remove();
			onRemove();
			onChange();
			return result;
		}
	}

	@Override
	public int setCapacity( final int newCapacity ) {
		if( newCapacity < 0 ) throw new IllegalArgumentException();

		try( final Unlocker unlocker = lock() ) {
			final int oldCapacity = values.getCapacity();
			if( newCapacity != oldCapacity ) {
				values.setCapacity( newCapacity );
				onCapacity( newCapacity );
				trimToCapacity();
				onChange();
			}
			return oldCapacity;
		}
	}

	@Override
	public int getCapacity() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.getCapacity();
		}
	}
}
