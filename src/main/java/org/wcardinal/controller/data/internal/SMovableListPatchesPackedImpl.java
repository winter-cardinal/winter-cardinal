/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.NavigableMap;
import java.util.TreeMap;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize( using=SPatchesPackedImplSerializer.class )
@JsonDeserialize( using=SMovableListPatchesPackedImplDeserializer.class )
public class SMovableListPatchesPackedImpl<V> extends SMovableListPatchesPacked<V, SMovableListPatch<V>> {
	public SMovableListPatchesPackedImpl( final long startRevision, final long endRevision, final SMovableListPatch<V> reset ) {
		super( startRevision, endRevision, reset );
	}

	public SMovableListPatchesPackedImpl( final long startRevision, final long endRevision, final NavigableMap<Long, SMovableListPatch<V>> revisionToPatch ) {
		super( startRevision, endRevision, revisionToPatch );
	}

	public static <V> SMovableListPatchesPackedImpl<V> deserialize( final JsonParser parser, final DeserializationContext ctxt, final JavaType valueType) throws JacksonException {
		SPatchUtil.assertCurrentToken( parser, JsonToken.START_ARRAY );

		parser.nextToken();
		final long startRevision = ctxt.readValue(parser, long.class);

		parser.nextToken();
		final long endRevision = ctxt.readValue(parser, long.class) + startRevision;

		parser.nextToken();
		final int type = ctxt.readValue(parser, int.class);

		if( type == 0 ) {
			parser.nextToken();
			final SMovableListPatchReset<V> reset = SMovableListPatchReset.<V>deserialize(parser, ctxt, valueType);
			return new SMovableListPatchesPackedImpl<V>( startRevision, endRevision, reset );
		} else {
			final NavigableMap<Long, SMovableListPatch<V>> revisionToPatch = new TreeMap<>();
			while( parser.nextToken() != JsonToken.END_ARRAY ) {
				final long revision = parser.getLongValue() + startRevision;
				parser.nextToken();
				final SMovableListPatchMap<V> patch = SMovableListPatchMap.<V>deserialize( parser, ctxt, valueType );
				revisionToPatch.put( revision, patch );
			}
			return new SMovableListPatchesPackedImpl<V>( startRevision, endRevision, revisionToPatch );
		}
	}
}
