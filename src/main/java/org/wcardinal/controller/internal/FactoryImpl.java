/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.wcardinal.controller.AbstractComponent;
import org.wcardinal.controller.Factory;
import org.wcardinal.util.json.Json;
import org.wcardinal.util.reflection.AbstractMethods;
import org.wcardinal.util.thread.Unlocker;

public class FactoryImpl<T, U extends Controller> extends AbstractComponent implements Factory<T>, ControllerCreatorAware<U> {
	final Logger logger = LoggerFactory.getLogger(FactoryImpl.class);

	ControllerCreator<U> creator;
	final Map<T, String> instanceToName = new HashMap<T, String>();

	String nextName(){
		String result = null;

		do {
			result = "a"+Long.toString(System.currentTimeMillis(), 32)
				+ Long.toString(Math.round(Math.random() * Long.MAX_VALUE), 32);
		} while( getFactoryData( result ) != null );

		return result;
	}

	@Override
	public void setControllerCreator( final ControllerCreator<U> creator ){
		this.creator = creator;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T create( final Object... arguments ){
		try( final Unlocker unlocker = lock() ) {
			final String name = nextName();
			final ArrayNode args = (ArrayNode) Json.mapper.valueToTree( arguments );
			final Controller controller = creator.createDynamic( name, args );
			final T instance = (T) controller.getInstance();
			controller.create( arguments );
			controller.checkVisibility();
			if( creator.isPostCreated() ) {
				controller.postCreate( arguments );
			}

			instanceToName.put( instance, name );
			creator.initDynamic( name, controller, args, arguments );

			return instance;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createAsRequested( final FactoryData data ){
		try( final Unlocker unlocker = lock() ) {
			final Controller controller = creator.createDynamic( data.name, data.args );
			instanceToName.put( (T) controller.getInstance(), data.name );

			final Object[] parameters = AbstractMethods.toParameters( data.args, 0 );
			controller.create( parameters );
			controller.checkVisibility();
			controller.postCreate( parameters );
		}
	}

	@Override
	public boolean contains( final Object instance ){
		try( final Unlocker unlocker = lock() ) {
			return instanceToName.containsKey( instance );
		}
	}

	@Override
	public int size(){
		try( final Unlocker unlocker = lock() ) {
			return instanceToName.size();
		}
	}

	@Override
	public boolean destroy( final Object instance ){
		try( final Unlocker unlocker = lock() ) {
			final String name = instanceToName.remove( instance );
			if( name != null ){
				for( final Iterator<FactoryData> i = creator.getFactoryData().iterator(); i.hasNext(); ){
					final FactoryData data = i.next();
					if( name.equals( data.name ) ){
						i.remove();
						break;
					}
				}

				return ( creator.destroyDynamic( name ) != null );
			}
		}
		return false;
	}

	FactoryData getFactoryData( final String name ){
		for( final FactoryData data: creator.getFactoryData() ){
			if( name.equals( data.name ) ) return data;
		}
		return null;
	}

	@Override
	public Object destroyByData( final FactoryData data ){
		if( data == null ) return null;

		try( final Unlocker unlocker = lock() ) {
			final U controller = creator.destroyDynamic( data.name );
			if( controller != null ) {
				instanceToName.remove( controller.getInstance() );
			}
			return controller;
		}
	}

	@Override
	public void clear() {
		try( final Unlocker unlocker = lock() ) {
			final Set<String> names = new HashSet<String>( instanceToName.values() );
			creator.getFactoryData().clear();
			instanceToName.clear();

			for( final String name: names ){
				creator.destroyDynamic( name );
			}
		}
	}

	@Override
	public Iterator<T> iterator() {
		try( final Unlocker unlocker = lock() ) {
			return new HashSet<T>( instanceToName.keySet() ).iterator();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(final int index) {
		try( final Unlocker unlocker = lock() ) {
			if( 0 <= index && index < creator.getFactoryData().size() ) {
				final FactoryData data = creator.getFactoryData().get( index );
				if( data != null ){
					final U controller = creator.getDynamic( data.name );
					if( controller != null ){
						return (T) controller.getInstance();
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return size() <= 0;
	}

	@Override
	public int indexOf(final T instance) {
		try( final Unlocker unlocker = lock() ) {
			for( int i=0; i<creator.getFactoryData().size(); ++i ){
				final FactoryData data = creator.getFactoryData().get( i );
				final U controller = creator.getDynamic( data.name );
				if( controller.getInstance() == instance ){
					return i;
				}
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T remove(final int index) {
		try( final Unlocker unlocker = lock() ) {
			if( 0 <= index && index < creator.getFactoryData().size() ) {
				final FactoryData data = creator.getFactoryData().remove( index );
				if( data != null ){
					final U controller = creator.destroyDynamic( data.name );
					if( controller != null ) {
						final T instance = (T) controller.getInstance();
						instanceToName.remove( instance );
						return instance;
					}
				}
			}
		}
		return null;
	}
}
