/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.internal.Controller;
import org.wcardinal.controller.internal.ControllerDynamicInfoHandler;
import org.wcardinal.controller.internal.Property;
import org.wcardinal.controller.internal.info.DynamicDataObject;
import org.wcardinal.controller.internal.info.SetDynamicDataMap;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

@Primary
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SListImpl<V> extends SList<V> implements SListContainer<V>, ControllerDynamicInfoHandler {
	String name = null;
	Controller controller = null;
	boolean isReadOnly = false;
	boolean isNonNull = false;
	boolean isSoft = false;
	boolean isInitialized = false;
	AutoCloseableReentrantLock lock;
	ResolvableType type;

	long revision = 0;
	final List<V> values = new ArrayList<>();
	final Map<Object, SListDataImpl<V>> originToData = new HashMap<>();

	public SListImpl() {}

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
	public List<V> getValue() {
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
			originToData.put( origin, new SListDataImpl<V>( this, origin ) );
		}
	}

	@Override
	public void removeOrigin( final Object origin ) {
		originToData.remove( origin );
	}

	@Override
	public void handle( final Object origin, final SetDynamicDataMap map ) {
		final SListDataImpl<V> data = originToData.get( origin );
		if( data != null ) data.handle( map );
	}

	@Override
	public void handle( final Object origin, final Map<String, SData> nameToSData, final Map<String, DynamicDataObject> nameToData, final long senderId ) {
		final SListDataImpl<V> data = originToData.get( origin );
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
	public boolean onAdd( final int index, final V value ) {
		for( final SListDataImpl<V> data: originToData.values() ) {
			data.onAdd( index, value );
		}
		return true;
	}

	@Override
	public boolean onAddAll( final int index, final Collection<? extends V> values ) {
		if( values.isEmpty() != true ) {
			for( final SListDataImpl<V> data: originToData.values() ) {
				data.onAddAll( index, values );
			}
			return true;
		}
		return false;
	}

	@Override
	public V onRemove( final int index, final V value ) {
		for( final SListDataImpl<V> data: originToData.values() ) {
			data.onRemove( index, value );
		}
		return value;
	}

	@Override
	public void onClear() {
		for( final SListDataImpl<V> data: originToData.values() ) {
			data.onClear();
		}
	}

	@Override
	public void onSet( final int index, final V newValue ) {
		for( final SListDataImpl<V> data: originToData.values() ) {
			data.onSet( index, newValue );
		}
	}

	@Override
	public void onChange() {
		revision += 1;
		isInitialized = true;
	}

	@Override
	public void onInitialize( final Object except ) {
		for( final SListDataImpl<V> data: originToData.values() ) {
			if( data == except ) continue;
			data.onInitialize();
		}
	}

	@Override
	public void onPatches( final Object except, final SPatchesPacked<List<V>, SListPatch<V>> patches ) {
		for( final SListDataImpl<V> data: originToData.values() ) {
			if( data == except ) continue;
			data.onPatches( patches );
		}
	}

	@Override
	public void toDirty( final int index ){
		try( final Unlocker unlocker = lock() ) {
			if( 0 <= index && index < size() ){
				set( index, get( index ) );
			}
		}
	}

	@Override
	public void toDirty(){
		try( final Unlocker unlocker = lock() ) {
			for( int i=0; i<values.size(); ++i ){
				set( i, get( i ) );
			}
		}
	}

	@Override
	public boolean add( final V value ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, value );
			final int index = values.size();
			values.add( value );
			onAdd( index, value );
			onChange();
			return true;
		}
	}

	@Override
	public void add( final int index, final V value ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, value );
			values.add(index, value);
			onAdd( index, value );
			onChange();
		}
	}

	@Override
	public boolean addAll( final Collection<? extends V> values ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, values );
			final int index = this.values.size();
			this.values.addAll( values );
			final boolean result = onAddAll( index, values );
			if( result ) onChange();
			return result;
		}
	}

	@Override
	public boolean addAll( final int index, final Collection<? extends V> values ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, values );
			this.values.addAll( index, values );
			final boolean result = onAddAll( index, values );
			if( result ) onChange();
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
	public boolean contains( final Object value ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.contains( value );
		}
	}

	@Override
	public boolean containsAll( final Collection<?> values ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return this.values.containsAll( values );
		}
	}

	@Override
	public V get( final int index ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.get( index );
		}
	}

	@Override
	public int indexOf( final Object value ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.indexOf( value );
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
			return new SListIterator<V>( this, this, 0 );
		}
	}

	@Override
	public int lastIndexOf( final Object value ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.lastIndexOf( value );
		}
	}

	@Override
	public java.util.ListIterator<V> listIterator() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SListIterator<V>( this, this, 0 );
		}
	}

	@Override
	public java.util.ListIterator<V> listIterator( final int index ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SListIterator<V>( this, this, index );
		}
	}

	@Override
	public boolean remove( final Object target ) {
		try( final Unlocker unlocker = lock() ) {
			final int index = values.indexOf( target );
			if( 0 <= index ) {
				onRemove( index, values.remove( index ) );
				onChange();
			}
			return  0 <= index;
		}
	}

	@Override
	public V remove( final int index ) {
		try( final Unlocker unlocker = lock() ) {
			final V result = onRemove( index, values.remove( index ) );
			onChange();
			return result;
		}
	}

	@Override
	public boolean removeAll( final Collection<?> targets ) {
		try( final Unlocker unlocker = lock() ) {
			boolean result = false;
			for( final Object target: targets ){
				result |= remove( target );
			}
			return result;
		}
	}

	@Override
	public boolean retainAll( final Collection<?> targets ) {
		try( final Unlocker unlocker = lock() ) {
			boolean result = false;
			for( int i = values.size()-1; 0 <= i; --i ) {
				final V value = values.get( i );

				if( targets.contains( value ) != true ) {
					values.remove( i );
					onRemove( i, value );
					result = true;
				}
			}
			if( result ) onChange();
			return result;
		}
	}

	@Override
	public V set( final int index, final V value ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, value );
			final V result = values.set( index, value );
			onSet( index, value );
			onChange();
			return result;
		}
	}

	@Override
	public V reset( final int index ) {
		try( final Unlocker unlocker = lock() ) {
			final V result = values.get( index );
			onSet( index, result );
			onChange();
			return result;
		}
	}

	@Override
	public int size() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.size();
		}
	}

	@Override
	public List<V> subList( final int fromIndex, final int toIndex ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SListSubList<V>( this, this, values, fromIndex, toIndex );
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
	public void replace( final Iterable<? extends V> values ) {
		SCheckers.checkNonNull( isNonNull, values );

		try( final Unlocker unlocker = lock() ) {
			int index = -1;
			for( final V value: values ){
				index = index + 1;

				if( index < size() ){
					// Replace
					final V oldValue = this.values.get( index );
					if( Objects.equals(oldValue, value) != true ) {
						set( index, value );
					}
				} else {
					// Add
					add( value );
				}
			}

			// Remove
			for( int i=size()-1; index < i; --i ){
				remove( i );
			}
		}
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
	public boolean clearAndAdd( final V value ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, value );

			// Clear
			if( values.isEmpty() != true ) {
				values.clear();
				onClear();
			}

			// Add
			values.add( value );
			onAdd( 0, value );

			// OnChange
			onChange();

			return true;
		}
	}

	@Override
	public boolean clearAndAddAll( final Collection<? extends V> values ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, values );

			boolean result = false;

			// Clear
			if( this.values.isEmpty() != true ) {
				result = true;
				this.values.clear();
				onClear();
			}

			// Add all
			this.values.addAll( values );
			if( onAddAll( 0, values ) ) result = true;

			// OnChange
			if( result ) onChange();

			return result;
		}
	}
}
