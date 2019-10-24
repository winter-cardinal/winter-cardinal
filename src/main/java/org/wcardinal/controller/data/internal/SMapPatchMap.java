/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;

import org.wcardinal.controller.data.SMap.Update;

public class SMapPatchMap<V> implements SMapPatch<V> {
	final NavigableMap<String, V> added;
	final NavigableSet<String> removed;

	public SMapPatchMap() {
		this.added = new TreeMap<>();
		this.removed = new TreeSet<>();
	}

	public SMapPatchMap( final NavigableMap<String, V> added, final NavigableSet<String> removed ) {
		this.added = added;
		this.removed = removed;
	}

	@Override
	public void put( final String key, final V value ) {
		removed.remove( key );
		added.put( key, value );
	}

	@Override
	public void putAll( final Map<? extends String, ? extends V> values ) {
		if( values != null ) {
			for( final Map.Entry<? extends String, ? extends V> entry: values.entrySet() ) {
				final String key = entry.getKey();
				final V value = entry.getValue();
				removed.remove( key );
				added.put( key, value );
			}
		}
	}

	@Override
	public void remove( final String key ) {
		added.remove( key );
		removed.add( key );
	}

	@Override
	public void removeAll( final Map<? extends String, ? extends V> values ) {
		if( values != null ) {
			for( final String key: values.keySet() ) {
				added.remove( key );
				removed.add( key );
			}
		}
	}

	@Override
	public int getWeight() {
		return added.size();
	}

	@Override
	public void apply( final NavigableMap<String, V> map, final NavigableMap<String, V> padded, final NavigableMap<String, V> premoved, final NavigableMap<String, Update<V>> pupdated ) {
		// Removed
		for( final String key: this.removed ) {
			if( map.containsKey( key ) ) {
				final V value = map.remove( key );
				pupdated.remove( key );
				if( padded.containsKey( key ) ) {
					padded.remove( key );
				} else {
					premoved.put( key, value );
				}
			}
		}

		// Added
		for( final Map.Entry<String, V> entry: this.added.entrySet() ) {
			final String key = entry.getKey();
			final V newValue = entry.getValue();
			if( map.containsKey( key ) ) {
				final V oldValue = map.put( key, newValue );
				final Update<V> update = pupdated.get( key );
				pupdated.put( key, new Update<V>( newValue, update != null ? update.getOldValue() : oldValue ) );
			} else {
				padded.put( key, newValue );
			}
			map.put( key, newValue );
		}
	}

	@Override
	public void serialize( final JsonGenerator gen ) throws IOException {
		// Added
		gen.writeObject( added );

		// Removed
		gen.writeObject( removed );
	}

	public static <V> SMapPatchMap<V> deserialize( final JsonParser parser, final DeserializationContext ctxt, final JavaType valueType ) throws IOException, JsonProcessingException {
		// ADDED
		SPatchUtil.assertCurrentToken( parser, JsonToken.START_OBJECT );
		final NavigableMap<String, V> added = new TreeMap<>();
		while( parser.nextToken() != JsonToken.END_OBJECT ) {
			final String key = parser.getText();
			final V value = SPatchUtil.<V>readNextValue( parser, ctxt, valueType );
			added.put( key, value );
		}

		// REMOVED
		SPatchUtil.assertNextToken( parser, JsonToken.START_ARRAY );
		final NavigableSet<String> removed = new TreeSet<>();
		while( parser.nextToken() != JsonToken.END_ARRAY ) {
			removed.add( parser.getText() );
		}

		// DONE
		return new SMapPatchMap<V>( added, removed );
	}

	@Override
	public boolean isReset() {
		return false;
	}
}
