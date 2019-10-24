/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

import org.wcardinal.controller.data.SMovableList.Move;
import org.wcardinal.controller.data.SList.Update;

public class SMovableListPatchMap<V> extends SListPatchMap<V> implements SMovableListPatch<V> {
	final List<Move<V>> newMoved;

	public SMovableListPatchMap() {
		super();
		newMoved = new ArrayList<>();
	}

	public SMovableListPatchMap( final NavigableMap<Integer, V> added, final NavigableSet<Integer> removed,
		final NavigableMap<Integer, V> updated, final List<Move<V>> newMoved ) {
		super( added, removed, updated );
		this.newMoved = newMoved;
	}

	@Override
	public void add( int index, final V value ) {
		SMovableListPatchMaps.add( index, value, added, updated, newMoved );
	}

	@Override
	public void addAll( int index, final Collection<? extends V> values ) {
		SMovableListPatchMaps.addAll( index, values, added, updated, newMoved );
	}

	@Override
	public void remove( int index ) {
		SMovableListPatchMaps.remove( index, added, removed, updated, newMoved );
	}

	@Override
	public void set( final int index, final V value ) {
		SMovableListPatchMaps.set( index, value, updated );
	}

	@Override
	public void move( final int oldIndex, final int newIndex ) {
		SMovableListPatchMaps.move( oldIndex, newIndex, updated, newMoved );
	}

	@Override
	public void apply( final List<V> list, final NavigableMap<Integer, V> padded, final NavigableMap<Integer, V> premoved,
			final NavigableMap<Integer, Update<V>> pupdated, final List<Move<V>> pnewMoved ) {
		SMovableListPatchMaps.apply(this.added, this.removed, this.updated, this.newMoved,
			list, padded, premoved, pupdated, pnewMoved);
	}

	@Override
	public void serialize( final JsonGenerator gen ) throws IOException {
		super.serialize( gen );

		// New moved
		gen.writeStartArray();
		for( final Move<V> move: newMoved ) {
			gen.writeNumber( move.getNewIndex() );
			gen.writeNumber( move.getOldIndex() );
		}
		gen.writeEndArray();
	}

	public static <V> SMovableListPatchMap<V> deserialize( final JsonParser parser, final DeserializationContext ctxt, final JavaType valueType ) throws IOException, JsonProcessingException {
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

		// NEW MOVED
		SPatchUtil.assertNextToken( parser, JsonToken.START_ARRAY );
		final List<Move<V>> newMoved = new ArrayList<>();
		while( parser.nextToken() != JsonToken.END_ARRAY ) {
			final int newIndex = parser.getIntValue();
			parser.nextToken();
			final int oldIndex = parser.getIntValue();
			newMoved.add( new Move<V>( newIndex, oldIndex, null ) );
		}

		// DONE
		return new SMovableListPatchMap<V>( added, removed, updated, newMoved );
	}
}
