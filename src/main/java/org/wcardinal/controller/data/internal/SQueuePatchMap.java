/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;

public class SQueuePatchMap<V> implements SQueuePatch<V> {
	final List<V> added;
	int removed;
	int capacity;

	public SQueuePatchMap() {
		this.added = new ArrayList<>();
		this.removed = 0;
		this.capacity = -1;
	}

	public SQueuePatchMap( final List<V> added, final int removed, final int capacity ) {
		this.added = added;
		this.removed = removed;
		this.capacity = capacity;
	}

	@Override
	public void add( final V value ) {
		this.added.add( value );
	}

	@Override
	public void addAll( final Collection<? extends V> values ) {
		this.added.addAll( values );
	}

	@Override
	public void remove() {
		this.removed += 1;
	}

	@Override
	public void capacity( final int capacity ) {
		this.capacity = capacity;
	}

	@Override
	public int getWeight() {
		return added.size();
	}

	@Override
	public void apply( final SQueueValues<V> queue, final List<V> padded, final List<V> premoved ) {
		queue.addAll( added );
		padded.addAll( added );

		for( int i=0; i<removed; ++i ) {
			final V value = queue.remove();
			premoved.add( value );
		}

		if( 0 <= capacity ) {
			queue.setCapacity( capacity );
		}
	}

	@Override
	public void serialize( final JsonGenerator gen ) throws JacksonException {
		// ADDED
		gen.writePOJO( added );

		// REMOVED
		gen.writePOJO( removed );

		// CAPACITY
		gen.writeNumber( capacity );
	}

	public static <V> SQueuePatchMap<V> deserialize( final JsonParser parser, final DeserializationContext ctxt, final JavaType valueType ) throws JacksonException {
		// ADDED
		SPatchUtil.assertCurrentToken( parser, JsonToken.START_ARRAY );
		final List<V> added = new ArrayList<>();
		while( parser.nextToken() != JsonToken.END_ARRAY ) {
			added.add( SPatchUtil.<V>readCurrentValue( parser, ctxt, valueType ) );
		}

		// REMOVED
		parser.nextToken();
		final int removed = parser.getIntValue();

		// CAPACITY
		parser.nextToken();
		final int capacity = parser.getIntValue();

		// DONE
		return new SQueuePatchMap<V>( added, removed, capacity );
	}

	@Override
	public boolean isReset() {
		return false;
	}
}
