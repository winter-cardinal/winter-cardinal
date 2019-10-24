/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;

public class SQueuePatchReset<V> implements SQueuePatch<V> {
	final List<V> values;
	final int capacity;

	public SQueuePatchReset( final List<V> values, final int capacity ) {
		this.values = values;
		this.capacity = capacity;
	}

	@Override
	public void add( final V value ) {
		// DO NOTHING
	}

	@Override
	public void addAll( final Collection<? extends V> values ) {
		// DO NOTHING
	}

	@Override
	public void remove() {
		// DO NOTHING
	}

	@Override
	public void capacity( final int capacity ) {
		// DO NOTHING
	}

	@Override
	public int getWeight() {
		return 1;
	}

	@Override
	public void apply( final SQueueValues<V> queue, final List<V> added, final List<V> removed ) {
		added.clear();
		removed.clear();

		if( 0 <= capacity ) {
			queue.setCapacity( capacity );
		}

		if( values.isEmpty() ) {
			removed.addAll( queue );
			queue.clear();
		} else {
			final V first = values.get( 0 );
			for( final SQueueValuesIterator<V> iterator = queue.iterator(); iterator.hasNext(); ) {
				final V value = iterator.next();
				if( Objects.equals( first, value ) != true ) continue;

				// REMOVE THE HEAD
				iterator.removeHead( removed );

				// SEARCH AND REMOVE THE TAIL
				int i=1;
				for( ; iterator.hasNext(); ++i ) {
					if( i < values.size() && Objects.equals( values.get( i ), iterator.next() ) ) continue;
					iterator.removeTail( removed );
					break;
				}

				// ADD THE REST
				if( i < values.size() ) {
					final List<V> addedValues = values.subList( i, values.size() );
					added.addAll( addedValues );
					queue.addAll( addedValues );
				}

				return;
			}

			removed.addAll( queue );
			queue.clear();

			added.addAll( values );
			queue.addAll( values );
		}
	}

	@Override
	public void serialize( final JsonGenerator gen ) throws IOException {
		gen.writeObject( values );
		gen.writeNumber( capacity );
	}

	public static <V> SQueuePatchReset<V> deserialize( final JsonParser parser, final DeserializationContext ctxt, final JavaType valueType ) throws IOException, JsonProcessingException {
		final JavaType valuesType = ctxt.getTypeFactory().constructCollectionType(List.class, valueType);
		final List<V> values = ctxt.readValue( parser, valuesType );
		parser.nextToken();
		final int capacity = parser.getIntValue();
		return new SQueuePatchReset<V>( values, capacity );
	}

	@Override
	public boolean isReset() {
		return true;
	}
}
