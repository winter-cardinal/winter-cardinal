/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "../util/lang/each";
import { Component } from "./component";
import { Controller } from "./controller";

/**
 * A factory that creates/destroys controllers dynamically.
 */
export interface Factory<V extends Controller> extends Component {
	/**
	 * Creates an instance and returns it.
	 * The specified arguments are passed to the instance.
	 * See {@link org.wcardinal.controller.AbstractController#getFactoryParameters()} of the javadoc.
	 *
	 * @param parameters factory parameters
	 * @returns the new instance
	 * @throws Error if this is read-only
	 */
	create( ...parameters: unknown[] ): V;

	/**
	 * Iterates over the instances this factory has invoking the specified function for the each instance.
	 * The specified function is invoked with the following:
	 *
	 * * instance
	 * * index
	 * * this factory
	 *
	 * By explicitly returning false, the specified function exits the iteration immediately.
	 *
	 * @param iteratee the function to be invoke for the each instance
	 * @param thisArg the this binding of the specified function
	 * @returns this
	 */
	each( iteratee: Iteratee<number, V, this>, thisArg: unknown ): this;
	each( iteratee: Iteratee<string, V, this>, thisArg: unknown ): this;

	/**
	 * Returns the array of the instances.
	 *
	 * @returns the array of the instances
	 */
	toArray(): V[];

	/**
	 * Returns the number of instances this factory has.
	 *
	 * @returns the number of instances this factory has.
	 */
	size(): number;

	/**
	 * Returns the instance at the specified index.
	 *
	 * @param index index of the instance
	 * @returns the instance at the specified index
	 */
	get( index: number ): V | null;

	/**
	 * Returns true if this factory is empty.
	 *
	 * @returns true if this factory is empty
	 */
	isEmpty(): boolean;

	/**
	 * Returns the index of the specified instance.
	 *
	 * @param instance instance to search for
	 * @returns the index of the specified instance or -1 if this factory does not contain the specified instance
	 */
	indexOf( instance: V ): number;

	/**
	 * Returns true if this factory has the specified instance.
	 *
	 * @param instance the instance to be checked
	 * @returns true if this factory has the specified instance
	 */
	contains( instance: V ): boolean;

	/**
	 * Removes the instance at the specified index.
	 *
	 * @param index index of the instance
	 * @returns the old instance at the specified index
	 * @throws Error if this is read-only
	 */
	remove( index: number ): V | null;

	/**
	 * Iterates instances of this factory, calling the iteratee for each instance,
	 * removing all instances the iteratee does not return true.
	 * The iteratee is bound to the thisArg and invoked with three arguments: instance, index and this factory.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @throws Error if this is read-only
	 * @returns this
	 */
	filter( iteratee: Iteratee<number, V, this>, thisArg: unknown ): this;

	/**
	 * Destroys the specified instance and returns true if succeeded.
	 *
	 * @param instance the instance to be destroyed
	 * @returns true if the specified instance is destroyed successfully
	 * @throws Error if this is read-only
	 */
	destroy( instance: V ): boolean;

	/**
	 * Destroys the all instances this factory has.
	 *
	 * @throws Error if this is read-only
	 * @returns this
	 */
	clear(): this;

	/**
	 * Returns the JSON array representing this factory.
	 *
	 * @returns the JSON array representing this factory
	 */
	toJson(): unknown;

	/**
	 * Returns the string representing the array of instances.
	 *
	 * @returns the string representing the array of instances
	 */
	toString(): string;

	/**
	 * Triggered when a page is created.
	 *
	 * @event create
	 * @param event an event object
	 * @param createdInstance a new page
	 * @param parameters factory parameters passed to [[create]]
	 * @internal
	 */
	oncreate?( event: Event, newInstance: V, parameters: unknown[] ): void;

	/**
	 * Triggered when a page is destroyed.
	 *
	 * @event destroy
	 * @param event an event object
	 * @param destroyedInstance a destroyed page
	 * @internal
	 */
	ondestroy?( event: Event, destroyedInstance: V, parameters: unknown[] ): void;
}
