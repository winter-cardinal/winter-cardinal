/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;

import org.wcardinal.controller.data.SMap.Update;

public class SMapPatchReset<V> implements SMapPatch<V> {
	final NavigableMap<String, V> values;

	public SMapPatchReset( final NavigableMap<String, V> values ) {
		this.values = values;
	}

	@Override
	public void put( final String key, final V value ) {
		// DO NOTHING
	}

	@Override
	public void putAll( final Map<? extends String, ? extends V> values ) {
		// DO NOTHING
	}

	@Override
	public void remove( final String key ) {
		// DO NOTHING
	}

	@Override
	public void removeAll( final Map<? extends String, ? extends V> values ) {
		// DO NOTHING
	}

	@Override
	public int getWeight() {
		return 1;
	}

	@Override
	public void apply( final NavigableMap<String, V> map, final NavigableMap<String, V> added, final NavigableMap<String, V> removed, final NavigableMap<String, Update<V>> updated ) {
		added.clear();
		removed.clear();
		updated.clear();

		for( final Iterator<Map.Entry<String, V>> i = map.entrySet().iterator(); i.hasNext(); ) {
			final Map.Entry<String, V> entry = i.next();
			final String key = entry.getKey();
			final V oldValue = entry.getValue();
			if( values.containsKey( key ) ) {
				final V newValue = values.get( key );
				if( Objects.equals( newValue, oldValue ) != true ) {
					updated.put( key, new Update<V>( newValue, oldValue ) );
					map.put( key, newValue );
				}
			} else {
				removed.put( key, oldValue );
				i.remove();
			}
		}

		for( final Map.Entry<String, V> entry: values.entrySet() ) {
			final String key = entry.getKey();
			final V value = entry.getValue();
			if( map.containsKey( key ) != true ) {
				map.put( key, value );
				added.put( key, value );
			}
		}
	}

	@Override
	public void serialize( final JsonGenerator gen ) throws JacksonException {
		gen.writePOJO( values );
	}

	public static <V> SMapPatchReset<V> deserialize( final JsonParser parser, final DeserializationContext ctxt, final JavaType valueType ) throws JacksonException {
		SPatchUtil.assertCurrentToken( parser, JsonToken.START_OBJECT );
		final NavigableMap<String, V> values = new TreeMap<>();
		while( parser.nextToken() != JsonToken.END_OBJECT ) {
			final String key = parser.getString();
			final V value = SPatchUtil.<V>readNextValue( parser, ctxt, valueType );
			values.put( key, value );
		}

		return new SMapPatchReset<V>( values );
	}

	@Override
	public boolean isReset() {
		return true;
	}
}
