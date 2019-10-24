/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.Collection;
import java.util.List;

public interface SQueuePatch<V> extends SPatch {
	void add( V value );
	void addAll( Collection<? extends V> values );
	void remove();
	void capacity( int capacity );
	void apply( SQueueValues<V> queue, List<V> padded, List<V> premoved );
}
