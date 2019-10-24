/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Map;

public interface SNavigableMapContainer<V> extends SMapContainer<V> {
	boolean onRemoveAll( Map<? extends String, ? extends V> values );
}
