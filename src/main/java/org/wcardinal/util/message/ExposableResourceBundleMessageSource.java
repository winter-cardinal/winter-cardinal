/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.message;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.context.support.ResourceBundleMessageSource;

public class ExposableResourceBundleMessageSource extends ResourceBundleMessageSource implements ExposableMessageSource {
	String[] basenames = new String[0];

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
		for( final String basename: basenames ){
			final ResourceBundle bundle = this.getResourceBundle(basename, locale);
			for( final String key: bundle.keySet() ){
				result.put(key, getMessage(key, null, locale));
			}
		}
		return result;
	}

	@Override
	public Map<String, String> getMap( final Locale locale, final Iterable<String> startsWiths ){
		final Map<String, String> result = new HashMap<String, String>();
		for( final String basename: basenames ){
			final ResourceBundle bundle = this.getResourceBundle(basename, locale);
			for( final String key: bundle.keySet() ){
				for( final String startsWith: startsWiths ){
					if( key.startsWith(startsWith) ){
						result.put(key, getMessage(key, null, locale));
						break;
					}
				}
			}
		}
		return result;
	}
}
