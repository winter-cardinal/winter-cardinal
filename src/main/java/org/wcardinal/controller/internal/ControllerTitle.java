/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import java.util.List;
import java.util.Locale;

import org.springframework.context.ApplicationContext;

public class ControllerTitle {
	final ApplicationContext context;
	final String[] separators;
	final String[] separatorMessages;

	public ControllerTitle( final ApplicationContext context, final String[] separators, final String[] separatorMessages ){
		this.context = context;
		this.separators = separators;
		this.separatorMessages = separatorMessages;
	}

	public String[] getSeparators( final List<Locale> locales ) {
		if( separatorMessages.length <= 0 ) {
			return separators;
		} else if( separatorMessages.length <= 1 ) {
			final String first = separatorMessages[ 0 ];
			final String second = (2 <= separators.length ? separators[ 1 ] : " / ");
			final Locale locale = (0 < locales.size() ? locales.get( 0 ) : Locale.getDefault());
			return new String[]{
				context.getMessage(first, null, first, locale),
				second
			};
		} else {
			final String first = separatorMessages[ 0 ];
			final String second = separatorMessages[ 1 ];
			final Locale locale = (0 < locales.size() ? locales.get( 0 ) : Locale.getDefault());
			return new String[]{
				context.getMessage(first, null, first, locale),
				context.getMessage(second, null, second, locale)
			};
		}
	}
}
