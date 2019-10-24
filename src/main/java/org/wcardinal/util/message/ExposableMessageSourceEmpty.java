/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.message;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

class ExposableMessageSourceEmpty implements ExposableMessageSource {
	@Override
	public Map<String, String> getMap( final Locale locale ) {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, String> getMap( final Locale locale, final Iterable<String> startsWiths ) {
		return getMap( locale );
	}
}
