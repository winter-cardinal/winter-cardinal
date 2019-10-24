/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import java.util.Queue;

/**
 * Represents a queue and is more efficient for data streams
 * compared to the {@link org.wcardinal.controller.data.SList}.
 * The default capacity is {@link Integer#MAX_VALUE}.
 * The following are not supported for efficiency reasons:
 *
 * * {@link Queue#remove(Object)},
 * * {@link Queue#removeAll(java.util.Collection)},
 * * {@link Queue#retainAll(java.util.Collection)}.
 *
 * @param <T> data type
 */
public abstract class SQueue<T> extends SROQueue<T> {

}
