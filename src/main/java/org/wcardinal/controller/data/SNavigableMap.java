/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import java.util.NavigableMap;

/**
 * Represents the navigable map data.
 * The key type is the string and must not be null.
 * The class {@code V} can be any class.
 * However, for synchronization, the class {@code V} must be serializable
 * to, and deserializable from JSON by the Jackson.
 *
 * @param <V> value type
 */
public abstract class SNavigableMap<V> extends SMap<V> implements NavigableMap<String, V> {
}
