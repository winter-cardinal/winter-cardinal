/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;

import org.wcardinal.controller.data.SList.Update;

public class SListPatchReset<V> implements SListPatch<V> {
	final List<V> values;

	public SListPatchReset( final List<V> values ) {
		this.values = values;
	}

	@Override
	public void add( final int index, final V value ) {
		// DO NOTHING
	}

	@Override
	public void addAll( final int index, final Collection<? extends V> values ) {
		// DO NOTHING
	}

	@Override
	public void remove( final int index ) {
		// DO NOTHING
	}

	@Override
	public void set( final int index, final V value ) {
		// DO NOTHING
	}

	@Override
	public int getWeight() {
		return 1;
	}

	int apply( final int index, final List<V> list, final NavigableMap<Integer, V> added, final NavigableMap<Integer, V> removed, final NavigableMap<Integer, Update<V>> updated ) {
		final int llen = list.size();
		if( index < llen ) {
			final V value = values.get( index );
			final V lvalue = list.get( index );
			if( Objects.equals( value, lvalue ) ) return index + 1;

			int mode = 0;
			int ilast = index+1;
			final int vlen = values.size();
			for( int i=index+1, imax=Math.min( index + 10, Math.max( llen, vlen ) ); i<imax; ++i ) {
				if( i < vlen && Objects.equals( lvalue, values.get( i ) ) ) {
					mode = 1;
					ilast = i;
					break;
				}

				if( i < llen && Objects.equals( value, list.get( i ) ) ) {
					mode = 2;
					ilast = i;
					break;
				}
			}

			if( mode != 0 ) {
				for( int i=index+1, imax=Math.min(ilast, Math.min( llen, vlen )); i<imax; ++i ) {
					if( Objects.equals( list.get( i ), values.get( i ) ) ) {
						mode = 3;
						ilast = i;
						break;
					}
				}
			}

			switch( mode ) {
			case 1:
				for( int i=index; i<ilast; ++i ) {
					final V newValue = values.get( i );
					list.add( i, newValue );
					added.put( i, newValue );
				}
				return ilast + 1;
			case 2:
				for( int i=index; i<ilast; ++i ) {
					final V oldValue = list.remove( index );
					removed.put( index + removed.size() - added.size(), oldValue );
				}
				return index + 1;
			default:
				if( updated == null ) {
					for( int i=index; i<ilast; ++i ) {
						final V newValue = values.get( i );
						final V oldValue = list.set( i, newValue );
						removed.put( i + removed.size() - added.size(), oldValue );
						added.put( i, newValue );
					}
				} else {
					for( int i=index; i<ilast; ++i ) {
						final V newValue = values.get( i );
						final V oldValue = list.set( i, newValue );
						updated.put( i, new Update<V>( newValue, oldValue ));
					}
				}
				return (mode == 3 ? ilast + 1 : ilast);
			}
		} else {
			final V value = values.get( index );
			added.put( index, value );
			list.add( value );
			return index + 1;
		}
	}

	@Override
	public void apply( final List<V> list, final NavigableMap<Integer, V> added, final NavigableMap<Integer, V> removed, final NavigableMap<Integer, Update<V>> updated ) {
		removed.clear();
		added.clear();
		updated.clear();

		for( int i=0; i<values.size(); ) {
			i = apply( i, list, added, removed, updated );
		}

		for( int imin=values.size(), i=imin, imax=list.size(); i<imax; ++i ) {
			removed.put( imin + removed.size(), list.remove( imin ) );
		}
	}

	@Override
	public void serialize( final JsonGenerator gen ) throws IOException {
		gen.writeObject( values );
	}

	public static <V> SListPatchReset<V> deserialize( final JsonParser parser, final DeserializationContext ctxt, final JavaType valueType ) throws IOException, JsonProcessingException {
		final JavaType valuesType = ctxt.getTypeFactory().constructCollectionType(List.class, valueType);
		final List<V> values = ctxt.readValue( parser, valuesType );
		return new SListPatchReset<V>( values );
	}

	@Override
	public boolean isReset() {
		return true;
	}
}
