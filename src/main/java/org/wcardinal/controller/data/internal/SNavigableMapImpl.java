/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import org.wcardinal.controller.data.SKeyOf;
import org.wcardinal.controller.data.SNavigableMap;
import org.wcardinal.controller.internal.Controller;
import org.wcardinal.controller.internal.ControllerDynamicInfoHandler;
import org.wcardinal.controller.internal.Property;
import org.wcardinal.controller.internal.info.DynamicDataObject;
import org.wcardinal.controller.internal.info.SetDynamicDataMap;
import org.wcardinal.util.thread.AutoCloseableReentrantLock;
import org.wcardinal.util.thread.Unlocker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SNavigableMapImpl<V> extends SNavigableMap<V> implements SNavigableMapContainer<V>, ControllerDynamicInfoHandler {
	String name = null;
	Controller controller = null;
	boolean isReadOnly = false;
	boolean isNonNull = false;
	boolean isSoft = false;
	boolean isInitialized = false;
	AutoCloseableReentrantLock lock;
	ResolvableType type;

	long revision = 0;
	NavigableMap<String, V> values = null;
	final Map<Object, SMapDataImpl<V>> originToData = new HashMap<>();

	SKeyOf<V> keyOf = null;

	public SNavigableMapImpl() {}

	@SuppressWarnings("unchecked")
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
		this.keyOf = new SKeyOfImpl<V>( (Class<V>) type.resolve(Object.class) );

		controller.put( this );
	}

	public void setAscendingComparator() {
		values = new TreeMap<>( new SNavigableMapAscendingComparator() );
	}

	public void setDescendingComparator() {
		values = new TreeMap<>( new SNavigableMapDescendingComparator() );
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
	public NavigableMap<String, V> getValue() {
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
			originToData.put( origin, new SMapDataImpl<V>( this, origin ) );
		}
	}

	@Override
	public void removeOrigin( final Object origin ) {
		originToData.remove( origin );
	}

	@Override
	public void handle( final Object origin, final SetDynamicDataMap map ) {
		final SMapDataImpl<V> data = originToData.get( origin );
		if( data != null ) data.handle( map );
	}

	@Override
	public void handle( final Object origin, final Map<String, SData> nameToSData, final Map<String, DynamicDataObject> nameToData, final long senderId ) {
		final SMapDataImpl<V> data = originToData.get( origin );
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
	public boolean onPut( final String key, final V value ) {
		for( final SMapDataImpl<V> data: originToData.values() ) {
			data.onPut( key, value );
		}
		return true;
	}

	@Override
	public boolean onPutAll( final Map<? extends String, ? extends V> values ) {
		if( values.isEmpty() != true ) {
			for( final SMapDataImpl<V> data: originToData.values() ) {
				data.onPutAll( values );
			}
			return true;
		}
		return false;
	}

	@Override
	public void onRemove( final String key ) {
		for( final SMapDataImpl<V> data: originToData.values() ) {
			data.onRemove( key );
		}
	}

	@Override
	public boolean onRemoveAll( final Map<? extends String, ? extends V> values ) {
		if( values.isEmpty() != true ) {
			for( final SMapDataImpl<V> data: originToData.values() ) {
				data.onRemoveAll( values );
			}
			return true;
		}
		return false;
	}

	@Override
	public void onClear() {
		for( final SMapDataImpl<V> data: originToData.values() ) {
			data.onClear();
		}
	}

	@Override
	public void onInitialize( final Object except ) {
		for( final SMapDataImpl<V> data: originToData.values() ) {
			if( data == except ) continue;
			data.onInitialize();
		}
	}

	@Override
	public void onPatches( final Object except, final SPatchesPacked<NavigableMap<String, V>, SMapPatch<V>> patches ) {
		for( final SMapDataImpl<V> data: originToData.values() ) {
			if( data == except ) continue;
			data.onPatches( patches );
		}
	}

	@Override
	public void onChange() {
		revision += 1;
		isInitialized = true;
	}

	@Override
	public void toDirty( final String key ){
		try( final Unlocker unlocker = lock() ) {
			if( values.containsKey( key ) ){
				put( key, values.get( key ) );
			}
		}
	}

	@Override
	public void toDirty(){
		try( final Unlocker unlocker = lock() ) {
			final Iterator<Map.Entry<String, V>> i = values.entrySet().iterator();
			while( i.hasNext() ){
				final Map.Entry<String, V> entry = i.next();
				put( entry.getKey(), entry.getValue() );
			}
		}
	}

	@Override
	public void clear() {
		try( final Unlocker unlocker = lock() ) {
			if( ! values.isEmpty() ){
				values.clear();
				onClear();
				onChange();
			}
		}
	}

	@Override
	public boolean containsKey( final Object key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.containsKey( key );
		}
	}

	@Override
	public boolean containsValue( final Object value ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.containsValue( value );
		}
	}

	@Override
	public Set<Map.Entry<String, V>> entrySet() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapEntrySet<V>( this, this, values, null, true, null, false, false );
		}
	}

	@Override
	public V get(final Object key) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.get(key);
		}
	}

	@Override
	public boolean isEmpty() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.isEmpty();
		}
	}

	@Override
	public Set<String> keySet() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( this, this, values, values );
		}
	}

	@Override
	public V put(final String key, final V value) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, value );
			V result = null;
			if( key != null ) {
				result = values.put( key, value );
				onPut( key, value );
				onChange();
			}
			return result;
		}
	}

	@Override
	public V reput( final String key ) {
		try( final Unlocker unlocker = lock() ) {
			V result = null;
			if( values.containsKey( key ) ) {
				result = values.get( key );
				onPut( key, result );
				onChange();
			}
			return result;
		}
	}

	@Override
	public void putAll(final Map<? extends String, ? extends V> mappings) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, mappings );
			values.putAll( mappings );
			onPutAll( mappings );
		}
	}

	@Override
	public V remove( final Object o ) {
		try( final Unlocker unlocker = lock() ) {
			V result = null;
			if( o instanceof String ) {
				final String key = (String) o;
				if( values.containsKey( key ) ) {
					result = values.remove( key );
					onRemove( key );
					onChange();
				}
			}
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
	public Collection<V> values() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapValues<V>( this, this, values, values );
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
		if( values == null ) throw new NullPointerException();
		if( keyOf == null ) throw new IllegalStateException();
		replace( values, keyOf );
	}

	@Override
	public void replace( final Iterable<? extends V> values, final SKeyOf<V> keyOf ) {
		SCheckers.checkNonNull( isNonNull, values );
		Objects.requireNonNull( keyOf );

		try( final Unlocker unlocker = lock() ) {
			final Set<String> keys = new HashSet<String>();
			for( final V value: values ){
				final String key = keyOf.keyOf( value );
				if( key == null || keys.add( key ) != true ) continue;

				if( this.values.containsKey( key ) ){
					// Replace
					final V old = this.values.get( key );
					if( Objects.equals(old, value) != true ) {
						put( key, value );
					}
				} else {
					// Add
					put( key, value );
				}
			}

			// Remove
			for( final Iterator<Map.Entry<String, V>> i=this.values.entrySet().iterator(); i.hasNext(); ){
				final String key = i.next().getKey();
				if( keys.contains( key ) != true ) {
					i.remove();
					onRemove( key );
					onChange();
				}
			}
		}
	}

	@Override
	public void replace( final Map<String, ? extends V> mappings ) {
		SCheckers.checkNonNull( isNonNull, mappings );

		try( final Unlocker unlocker = lock() ) {
			for( final Map.Entry<String, ? extends V> entry: mappings.entrySet() ){
				final V value = entry.getValue();
				final String key = entry.getKey();

				if( values.containsKey( key ) ){
					// Replace
					final V old = this.values.get( key );
					if( Objects.equals(old, value) != true ) {
						put( key, value );
					}
				} else {
					// Add
					put( key, value );
				}
			}

			// Remove
			for( final Iterator<Map.Entry<String, V>> i=values.entrySet().iterator(); i.hasNext(); ){
				final String key = i.next().getKey();
				if( mappings.containsKey( key ) != true ) {
					i.remove();
					onRemove( key );
					onChange();
				}
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
	public Comparator<? super String> comparator() {
		return values.comparator();
	}

	@Override
	public String firstKey() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.firstKey();
		}
	}

	@Override
	public SortedMap<String, V> headMap( final String toKey ) {
		if( toKey == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapImpl<V>( this, this, values, null, true, toKey, false, false );
		}
	}

	@Override
	public String lastKey() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.lastKey();
		}
	}

	@Override
	public SortedMap<String, V> subMap( final String fromKey, final String toKey ) {
		if( fromKey == null || toKey == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			 return new SNavigableMapSubMapImpl<V>( this, this, values, fromKey, true, toKey, false, false );
		}
	}

	@Override
	public SortedMap<String, V> tailMap( final String fromKey ) {
		if( fromKey == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapImpl<V>( this, this, values, fromKey, true, null, false, false );
		}
	}

	@Override
	public Map.Entry<String, V> ceilingEntry( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( values.ceilingEntry( key ), this, this );
		}
	}

	@Override
	public String ceilingKey( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.ceilingKey( key );
		}
	}

	@Override
	public NavigableSet<String> descendingKeySet() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( this, this, values, values.descendingMap() );
		}
	}

	@Override
	public NavigableMap<String, V> descendingMap() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapImpl<V>( this, this, values, null, true, null, false, true );
		}
	}

	@Override
	public Map.Entry<String, V> firstEntry() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( values.firstEntry(), this, this );
		}
	}

	@Override
	public Map.Entry<String, V> floorEntry( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( values.floorEntry( key ), this, this );
		}
	}

	@Override
	public String floorKey( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.floorKey( key );
		}
	}

	@Override
	public NavigableMap<String, V> headMap( final String toKey, final boolean inclusive ) {
		if( toKey == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapImpl<V>( this, this, values, null, true, toKey, inclusive, false );
		}
	}

	@Override
	public Map.Entry<String, V> higherEntry( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( values.higherEntry( key ), this, this );
		}
	}

	@Override
	public String higherKey( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.higherKey( key );
		}
	}

	@Override
	public Map.Entry<String, V> lastEntry() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( values.lastEntry(), this, this );
		}
	}

	@Override
	public Map.Entry<String, V> lowerEntry( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return SMapEntry.create( values.lowerEntry( key ), this, this );
		}
	}

	@Override
	public String lowerKey( final String key ) {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return values.lowerKey( key );
		}
	}

	@Override
	public NavigableSet<String> navigableKeySet() {
		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapKeySet<V>( this, this, values, values );
		}
	}

	@Override
	public Map.Entry<String, V> pollFirstEntry() {
		try( final Unlocker unlocker = lock() ) {
			final Map.Entry<String, V> entry = values.pollFirstEntry();
			if( entry != null ){
				onRemove( entry.getKey() );
				onChange();
			}
			return entry;
		}
	}

	@Override
	public Map.Entry<String, V> pollLastEntry() {
		try( final Unlocker unlocker = lock() ) {
			final Map.Entry<String, V> entry = values.pollLastEntry();
			if( entry != null ){
				onRemove( entry.getKey() );
				onChange();
			}
			return entry;
		}
	}

	@Override
	public NavigableMap<String, V> subMap( final String fromKey, final boolean includeFrom, final String toKey, final boolean includeTo ) {
		if( fromKey == null || toKey == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapImpl<V>( this, this, values, fromKey, includeFrom, toKey, includeTo, false );
		}
	}

	@Override
	public NavigableMap<String, V> tailMap( final String fromKey, final boolean inclusive ) {
		if( fromKey == null ) throw new NullPointerException();

		try( final AutoCloseableReentrantLock closeable = lock.open() ) {
			return new SNavigableMapSubMapImpl<V>( this, this, values, fromKey, inclusive, null, false, false );
		}
	}

	@Override
	public V clearAndPut( final String key, final V value ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, value );
			clear();
			return put( key, value );
		}
	}

	@Override
	public void clearAndPutAll( final Map<? extends String, ? extends V> mappings ) {
		try( final Unlocker unlocker = lock() ) {
			SCheckers.checkNonNull( isNonNull, mappings );
			clear();
			putAll( mappings );
		}
	}
}
