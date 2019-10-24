/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import java.util.Iterator;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * A factory that creates/destroys the specified class {@code T} dynamically.
 *
 * @param <T> class to be created/destroyed dynamically
 */
public interface Factory<T> extends Iterable<T>, ControllerContext {
	/**
	 * Creates a instance of the class T and returns it.
	 *
	 * @param arguments factory arguments
	 * @return the new instance of the class T
	 */
	@ThreadSafe
	T create( final Object... arguments );

	/**
	 * Returns true if this factory has the specified instance.
	 *
	 * @param instance the instance to be checked
	 * @return true if this factory has the specified instance
	 */
	@ThreadSafe
	boolean contains( final Object instance );

	/**
	 * Returns the number of instances this factory has.
	 *
	 * @return the number of instances this factory has.
	 */
	@ThreadSafe
	int size();

	/**
	 * Returns the instance at the specified index.
	 *
	 * @param index index of the instance
	 * @return the instance at the specified index
	 */
	@ThreadSafe
	T get( final int index );

	/**
	 * Returns true if this factory is empty.
	 *
	 * @return true if this factory is empty
	 */
	@ThreadSafe
	boolean isEmpty();

	/**
	 * Returns the index of the specified instance.
	 *
	 * @param instance instance to search for
	 * @return the index of the specified instance or -1 if this factory does not contain the specified instance
	 */
	@ThreadSafe
	int indexOf( final T instance );

	/**
	 * Removes the instance at the specified index.
	 *
	 * @param index index of the instance
	 * @return the instance at the specified index
	 */
	@ThreadSafe
	T remove( final int index );

	/**
	 * Destroys the specified instance and returns true if succeeded.
	 *
	 * @param instance the instance to be destroyed
	 * @return true if the specified instance is destroyed successfully
	 */
	@ThreadSafe
	boolean destroy( final Object instance );

	/**
	 * Destroys the all instances this factory has.
	 */
	@ThreadSafe
	void clear();

	/**
	 * Returns the iterator over instances this factory has.
	 */
	@ThreadSafe
	Iterator<T> iterator();
}
