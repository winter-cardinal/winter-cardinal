/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;
import java.util.List;
import java.util.NavigableMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;

import org.wcardinal.controller.data.SMovableList.Move;
import org.wcardinal.controller.data.SList.Update;

public class SMovableListPatchReset<V> extends SListPatchReset<V> implements SMovableListPatch<V> {
	public SMovableListPatchReset( final List<V> values ) {
		super( values );
	}

	@Override
	public void move( final int oldIndex, final int newIndex ) {
		// DO NOTHING
	}

	@Override
	public void apply( final List<V> list, final NavigableMap<Integer, V> added, final NavigableMap<Integer, V> removed, final NavigableMap<Integer, Update<V>> updated, final List<Move<V>> newMoved ) {
		newMoved.clear();
		apply( list, added, removed, updated );
	}

	public static <V> SMovableListPatchReset<V> deserialize( final JsonParser parser, final DeserializationContext ctxt, final JavaType valueType ) throws IOException, JsonProcessingException {
		final JavaType valuesType = ctxt.getTypeFactory().constructCollectionType(List.class, valueType);
		final List<V> values = ctxt.readValue( parser, valuesType );
		return new SMovableListPatchReset<V>( values );
	}
}
