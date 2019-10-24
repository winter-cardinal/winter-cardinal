/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.HashMap;
import java.util.Map;

public class Info<T, U extends Info<T, U>> {
	public Map<String, T> nameToData;
	public Map<String, U> nameToInfo;

	public Info(){
		nameToData = null;
		nameToInfo = null;
	}

	public Info( final Map<String, T> nameToData, final Map<String, U> nameToInfo ) {
		this.nameToData = nameToData;
		this.nameToInfo = nameToInfo;
	}

	public Info( final U other ) {
		merge( other );
	}

	public void put( final String name, T data ) {
		if( nameToData == null ) {
			nameToData = new HashMap<>();
		}
		nameToData.put( name, data );
	}

	public void put( final String name, U info ) {
		if( nameToInfo == null ) {
			nameToInfo = new HashMap<>();
		}
		nameToInfo.put( name, info );
	}

	public void putAllData( final Map<String, ? extends T> other ) {
		if( other != null && other.isEmpty() != true ) {
			if( nameToData == null ) {
				nameToData = new HashMap<>();
			}
			nameToData.putAll( other );
		}
	}

	public void putAllInfo( final Map<String, ? extends U> other ) {
		if( other != null && other.isEmpty() != true ) {
			if( nameToInfo == null ) {
				nameToInfo = new HashMap<>();
			}
			nameToInfo.putAll( other );
		}
	}

	public void merge( final U other ) {
		if( other != null ) {
			if( other.nameToData != null ) {
				if( nameToData == null ) {
					nameToData = new HashMap<>();
				}
				for( final Map.Entry<String, T> entry: other.nameToData.entrySet() ) {
					final String name = entry.getKey();
					final T otherData = entry.getValue();
					final T data = nameToData.get( name );
					if( data != null ) {
						nameToData.put( name, merge( data, otherData ) );
					} else {
						nameToData.put( name, otherData );
					}
				}
			}

			if( other.nameToInfo != null ) {
				if( nameToInfo == null ) {
					nameToInfo = new HashMap<>();
				}
				for( final Map.Entry<String, U> entry: other.nameToInfo.entrySet() ) {
					final String name = entry.getKey();
					final U otherInfo = entry.getValue();
					final U info = nameToInfo.get( name );
					if( info != null ) {
						info.merge( otherInfo );
					} else {
						nameToInfo.put( name, clone( otherInfo ) );
					}
				}
			}
		}
	}

	T merge( final T data, final T otherData ) {
		return otherData;
	}

	U clone( U target ) {
		throw new UnsupportedOperationException();
	}
}
