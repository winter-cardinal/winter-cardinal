/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.message;

import java.util.Locale;
import java.util.Map;


public interface ExposableMessageSource {
	Map<String, String> getMap( final Locale locale );
	Map<String, String> getMap( final Locale locale, final Iterable<String> startsWiths );
}
