/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
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

import org.wcardinal.controller.data.SList.Update;

public class SListPatchMap<V> implements SListPatch<V> {
	final NavigableMap<Integer, V> added;
	final NavigableSet<Integer> removed;
	final NavigableMap<Integer, V> updated;

	public SListPatchMap() {
		this.added = new TreeMap<>();
		this.removed = new TreeSet<>();
		this.updated = new TreeMap<>();
	}

	public SListPatchMap( final NavigableMap<Integer, V> added, final NavigableSet<Integer> removed, final NavigableMap<Integer, V> updated ) {
		this.added = added;
		this.removed = removed;
		this.updated = updated;
	}

	@Override
	public void add( final int index, final V value ) {
		SListPatchMaps.add( index, value, added, updated );
	}

	@Override
	public void addAll( final int index, final Collection<? extends V> values ) {
		SListPatchMaps.addAll( index, values, added, updated );
	}

	@Override
	public void remove( final int index ) {
		SListPatchMaps.remove( index, added, removed, updated );
	}

	@Override
	public void set( final int index, final V value ) {
		SListPatchMaps.set( index, value, updated );
	}

	@Override
	public int getWeight() {
		return added.size() + updated.size();
	}

	@Override
	public void apply( final List<V> list, final NavigableMap<Integer, V> padded, final NavigableMap<Integer, V> premoved, final NavigableMap<Integer, Update<V>> pupdated ) {
		SListPatchMaps.apply( this.added, this.removed, this.updated, list, padded, premoved, pupdated );
	}

	@Override
	public void serialize( final JsonGenerator gen ) throws IOException {
		// Added
		gen.writeStartArray();
		for( final Map.Entry<Integer, V> entry: added.entrySet() ) {
			gen.writeNumber( entry.getKey() );
			gen.writeObject( entry.getValue() );
		}
		gen.writeEndArray();

		// Removed
		gen.writeObject( removed );

		// Updated
		gen.writeStartArray();
		for( final Map.Entry<Integer, V> entry: updated.entrySet() ) {
			gen.writeNumber( entry.getKey() );
			gen.writeObject( entry.getValue() );
		}
		gen.writeEndArray();
	}

	public static <V> SListPatchMap<V> deserialize( final JsonParser parser, final DeserializationContext ctxt, final JavaType valueType ) throws IOException, JsonProcessingException {
		// ADDED
		SPatchUtil.assertCurrentToken( parser, JsonToken.START_ARRAY );
		final NavigableMap<Integer, V> added = new TreeMap<>();
		while( parser.nextToken() != JsonToken.END_ARRAY ) {
			final int index = parser.getIntValue();
			final V value = SPatchUtil.<V>readNextValue( parser, ctxt, valueType );
			added.put( index, value );
		}

		// REMOVED
		SPatchUtil.assertNextToken( parser, JsonToken.START_ARRAY );
		final NavigableSet<Integer> removed = new TreeSet<>();
		while( parser.nextToken() != JsonToken.END_ARRAY ) {
			removed.add( parser.getIntValue() );
		}

		// UPDATED
		SPatchUtil.assertNextToken( parser, JsonToken.START_ARRAY );
		final NavigableMap<Integer, V> updated = new TreeMap<>();
		while( parser.nextToken() != JsonToken.END_ARRAY ) {
			final int index = parser.getIntValue();
			final V value = SPatchUtil.<V>readNextValue( parser, ctxt, valueType );
			updated.put( index, value );
		}

		// DONE
		return new SListPatchMap<V>( added, removed, updated );
	}

	@Override
	public boolean isReset() {
		return false;
	}
}
