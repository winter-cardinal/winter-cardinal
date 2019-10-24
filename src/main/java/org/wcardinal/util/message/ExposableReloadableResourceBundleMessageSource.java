/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.message;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class ExposableReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource implements ExposableMessageSource {
	int cacheSeconds = -1;
	String[] basenames = new String[0];

	@Override
	public void setCacheSeconds( final int cacheSeconds ) {
		this.cacheSeconds = cacheSeconds;
		super.setCacheSeconds( cacheSeconds );
	}

	@Override
	public void setBasenames( final String... basenames ){
		this.basenames = new String[ basenames.length ];
		for( int i=0; i<basenames.length; ++i ) {
			this.basenames[ i ] = basenames[ i ];
		}
		super.setBasenames( basenames );
	}

	@Override
	public Map<String, String> getMap( final Locale locale ){
		final Map<String, String> result = new HashMap<String, String>();
		if( this.cacheSeconds < 0 ) {
			put( result, getMergedProperties( locale ) );
		} else {
			for (final String basename : this.basenames) {
				final List<String> filenames = calculateAllFilenames(basename, locale);
				for (final String filename : filenames) {
					put( result, getProperties( filename ) );
				}
			}
		}
		return result;
	}

	@Override
	public Map<String, String> getMap( final Locale locale, final Iterable<String> startsWiths ){
		final Map<String, String> result = new HashMap<String, String>();
		if( this.cacheSeconds < 0 ) {
			put( result, getMergedProperties( locale ), startsWiths );
		} else {
			for (final String basename : this.basenames) {
				final List<String> filenames = calculateAllFilenames(basename, locale);
				for (final String filename : filenames) {
					put( result, getProperties( filename ), startsWiths );
				}
			}
		}
		return result;
	}

	void put( final Map<String, String> result, final PropertiesHolder holder ){
		if( holder == null ) return;

		final Properties properties = holder.getProperties();
		if( properties == null ) return;

		for( Map.Entry<Object, Object> entry: properties.entrySet() ){
			final Object keyObject = entry.getKey();
			final Object valueObject = entry.getValue();
			if( keyObject instanceof String && valueObject instanceof String ){
				final String key = (String) keyObject;
				final String value = (String) valueObject;
				if( result.containsKey(key) != true ) {
					result.put( key, value );
				}
			}
		}
	}

	void put( final Map<String, String> result, final PropertiesHolder holder, final Iterable<String> startsWiths ){
		if( holder == null ) return;

		final Properties properties = holder.getProperties();
		if( properties == null ) return;

		for( Map.Entry<Object, Object> entry: properties.entrySet() ){
			final Object keyObject = entry.getKey();
			final Object valueObject = entry.getValue();
			if( keyObject instanceof String && valueObject instanceof String ){
				final String key = (String) keyObject;
				final String value = (String) valueObject;
				if( result.containsKey(key) != true ) {
					for( final String startsWith: startsWiths ) {
						if( key.startsWith(startsWith) ){
							result.put( key, value );
							break;
						}
					}
				}
			}
		}
	}
}
