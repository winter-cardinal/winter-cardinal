/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.message;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import org.wcardinal.util.json.Json;

@Service
public class ExposableMessages {
	final Logger logger = LoggerFactory.getLogger(ExposableMessages.class);

	@Autowired
	ApplicationContext context;

	public ExposableMessageSource findExposableMessageSource(){
		final ExposableMessageSource source = context.getBean(ExposableMessageSource.class);
		if( source != null ) {
			return source;
		} else {
			logger.error("Failed to find a bean extending org.wcardinal.util.message.ExposableMessageSource");
			return new ExposableMessageSourceEmpty();
		}
	}

	public Map<String, String> getMap( final Locale locale ) {
		return findExposableMessageSource().getMap( locale );
	}

	public Map<String, String> getMap( final Locale locale, final String startsWith ){
		return getMap( locale, Arrays.asList(startsWith) );
	}

	public Map<String, String> getMap( final Locale locale, final Iterable<String> startsWiths ){
		return findExposableMessageSource().getMap( locale, startsWiths );
	}

	String toStringifiedMap( final Map<String, String> messages ) {
		try {
			return Json.convert( messages );
		} catch( Exception e ) {
			return "{}";
		}
	}

	String toScript( final Map<String, String> messages ) {
		return "(function(){"
		+     "var source = " + toStringifiedMap( messages ) + ";"
		+     "wcardinal.util.util.merge( wcardinal.util.MessageSource.prototype.source, source );"
		+ "}());";
	}

	public String getScript( final Locale locale ) {
		return toScript( getMap( locale ) );
	}

	public String getScript( final Locale locale, final String startsWith ){
		return toScript( getMap( locale, startsWith ) );
	}

	public String getScript( final Locale locale, final Iterable<String> startsWiths ){
		return toScript( getMap( locale, startsWiths ) );
	}
}
