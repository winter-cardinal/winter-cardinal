/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

/**
 * A factory that creates/destroys the specified class {@code T} as a popup dynamically.
 *
 * @param <T> popup class to be created/destroyed dynamically
 */
public interface PopupFactory<T> extends Factory<T> {

}
