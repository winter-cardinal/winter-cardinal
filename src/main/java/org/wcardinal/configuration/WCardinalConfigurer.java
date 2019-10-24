/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.configuration;

/**
 * Defines a callback method to configure Winter Cardinal.
 */
public interface WCardinalConfigurer {
	void configure( final WCardinalConfiguration configuration );
}
