/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

/**
 * A factory that creates/destroys the specified class T as a component dynamically.
 *
 * @param <T> component class to be created/destroyed dynamically
 */
public interface ComponentFactory<T> extends Factory<T> {

}
