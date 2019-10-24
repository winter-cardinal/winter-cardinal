/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize( using=SPatchesPackedImplSerializer.class )
@JsonDeserialize( using=SListPatchesPackedImplDeserializer.class )
public class SListPatchesPackedImpl<V> extends SListPatchesPacked<V, SListPatch<V>> {
	public SListPatchesPackedImpl( final long startRevision, final long endRevision, final SListPatch<V> reset ) {
		super( startRevision, endRevision, reset );
	}

	public SListPatchesPackedImpl( final long startRevision, final long endRevision, final NavigableMap<Long, SListPatch<V>> revisionToPatch ) {
		super( startRevision, endRevision, revisionToPatch );
	}

	public static <V> SListPatchesPackedImpl<V> deserialize( final JsonParser parser, final DeserializationContext ctxt, final JavaType valueType) throws IOException {
		SPatchUtil.assertCurrentToken( parser, JsonToken.START_ARRAY );

		parser.nextToken();
		final long startRevision = ctxt.readValue(parser, long.class);

		parser.nextToken();
		final long endRevision = ctxt.readValue(parser, long.class) + startRevision;

		parser.nextToken();
		final int type = ctxt.readValue(parser, int.class);

		if( type == 0 ) {
			parser.nextToken();
			final SListPatchReset<V> reset = SListPatchReset.<V>deserialize(parser, ctxt, valueType);
			return new SListPatchesPackedImpl<V>( startRevision, endRevision, reset );
		} else {
			final NavigableMap<Long, SListPatch<V>> revisionToPatch = new TreeMap<>();
			while( parser.nextToken() != JsonToken.END_ARRAY ) {
				final long revision = parser.getLongValue() + startRevision;
				parser.nextToken();
				final SListPatchMap<V> patch = SListPatchMap.<V>deserialize( parser, ctxt, valueType );
				revisionToPatch.put( revision, patch );
			}
			return new SListPatchesPackedImpl<V>( startRevision, endRevision, revisionToPatch );
		}
	}
}
