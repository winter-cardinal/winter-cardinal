/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "./lang/each";
import { PlainObject } from "./lang/plain-object";

/**
 * Provides utility methods.
 */
export interface Util {
	/**
	 * Registers the specified instance at the specified path.
	 * Setting the third argument `lowerCase` to true,
	 * the first character of the last part of the dot-separated path is changed to the lower case.
	 *
	 *     // Registers `this` instance to wcardinal.util.Util
	 *     util.register( "wcardinal.util.Util", this );
	 *
	 *     // Registers `this` instance to wcardinal.util.util
	 *     util.register( "wcardinal.util.Util", this, true );
	 *
	 * @param path the dot-separated path
	 * @param instance the instance to be registered
	 * @param lowerCase true for making the first character of the last part of the dot-separated path
	 * @param root a root object at which the specified instance being registered
	 * @returns the registered instance
	 */
	register<T>( path: string, instance: T, lowerCase?: boolean, root?: any ): T;

	getGlobal(): any;

	/**
	 * Returns Base64-encoded string of the specified string.
	 *
	 * @param string string to be decoded
	 * @returns encoded string
	 */
	btoa( string: string ): string;

	/**
	 * Returns the string of the specified Base64-encoded string.
	 *
	 * @param string Base64-encoded string
	 * @returns decoded string
	 */
	atob( base64: string ): string;

	/**
	 * Returns the bound function of the specified function.
	 *
	 * @param target a target function
	 * @param thisArg the this binding of the target function
	 * @param args the arguments to be passed to the target function
	 * @returns a bound function
	 */
	bind( targetFunction: Function, thisArg: unknown ): Function;

	/**
	 * Returns UNIX milliseconds.
	 *
	 * @returns UNIX milliseconds
	 */
	now(): number;

	/**
	 * Returns true if the specified target is a number.
	 *
	 * @param target a test target
	 * @returns true if the specified target is a number
	 */
	isNumber( target: any ): target is number;

	/**
	 * Returns true if the specified target is a NaN.
	 *
	 * @param target a test target
	 * @returns true if the specified target is a NaN
	 */
	isNaN( target: any ): boolean;

	/**
	 * Returns true if the specified target is a string.
	 *
	 * @param target a test target
	 * @returns true if the specified target is a string
	 */
	isString( target: any ): target is string;

	/**
	 * Returns true if the specified target is a function.
	 *
	 * @param target a test target
	 * @returns true if the specified target is a function
	 */
	isFunction( target: any ): target is Function;

	/**
	 * Returns true if the specified target is a thenable.
	 *
	 * @param target a test target
	 * @returns true if the specified target is a thenable
	 */
	isThenable( target: any ): target is PromiseLike<unknown>;

	/**
	 * Returns true if the specified target is a boolean.
	 *
	 * @param target a test target
	 * @returns true if the specified target is a boolean
	 */
	isBoolean( target: any ): target is boolean;

	/**
	 * Returns true if the specified target is an array.
	 *
	 * @param target a test target
	 * @returns true if the specified target is an array
	 */
	isArray( target: any ): target is unknown[];

	/**
	 * Returns true if the specified target is array-like.
	 *
	 * @param target a test target
	 * @returns true if the specified target is array-like
	 */
	isArrayLike( target: any ): target is ArrayLike<unknown>;

	/**
	 * Returns true if the specified target is a plain object.
	 *
	 * @param target a test target
	 * @returns true if the specified target is a plain object
	 */
	isPlainObject( target: any ): target is PlainObject;

	/**
	 * Returns true if the specified target is empty.
	 *
	 * @param target a test target
	 * @returns true if the specified target is empty
	 */
	isEmpty( target: any ): boolean;

	/**
	 * Returns the size of the specified target.
	 *
	 * @param target an inspection target
	 * @returns the size of the specified target
	 */
	size( target: any ): number;

	/**
	 * Returns true if the specified targets are equal to each other.
	 *
	 * @param a a test target
	 * @param b a test target
	 * @returns true if the specified targets are equal to each other
	 */
	isEqual( a: any, b: any ): boolean;

	/**
	 * Returns the lowest index at which the specified value should be inserted
	 * into the specified target in order to maintain its sort order.
	 *
	 * @param T a type of value
	 * @param target a sorted array
	 * @param value a test value
	 * @returns the lowest index at which the specified value should be inserted
	 */
	sortedIndex<T>( target: ArrayLike<T>, value: T ): number;

	/**
	 * Iterates over elements of the specified target, calling the iteratee for each element.
	 * The iteratee is bound to the thisArg and invoked with three arguments: value, field name/index and the target.
	 * The iteratee may exit iteration early by explicitly returning false.
	 *
	 * @param target a target collection
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @returns the specified target
	 */
	each<T, V>(	target: T, iteratee: Iteratee<string, V, T> | Iteratee<number, V, T>, thisArg?: unknown ): T;

	/**
	 * Iterates over elements of the specified target from right to left, calling the iteratee for each element.
	 * The iteratee is bound to the thisArg and invoked with three arguments: value, index and this list.
	 * The iteratee may exit iteration early by explicitly returning false.
	 *
	 * @param target a target collection
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @returns the specified target
	 */
	eachRight<T, V>( target: T, iteratee: Iteratee<string, V, T> | Iteratee<number, V, T>, thisArg?: unknown ): T;

	/**
	 * Recursively merges properties of source objects into the destination object.
	 * Undefined source properties are skipped if a corresponding destination property exits.
	 *
	 * @param destination
	 * @param sources
	 * @returns the destination object
	 */
	merge<T>( destination: T, ...sources: any[] ): T;

	/**
	 * Returns a shallow clone of the specified target.
	 * This method only clones arrays and plain objects in the specified target.
	 * Everything else is just copied to the cloned target from the specified target as is.
	 *
	 * @param target a clone target
	 * @returns a cloned target
	 */
	clone( target: unknown ): unknown;

	/**
	 * Returns a clone of the specified target.
	 * This method only clones arrays and plain objects in the specified target.
	 * Everything else is just copied to the cloned target from the specified target as is.
	 *
	 * @param target a clone target
	 * @returns a cloned target
	 */
	cloneDeep( target: unknown ): unknown;

	/**
	 * Returns true if the specified target has an own property of the specified name.
	 *
	 * @param target
	 * @param name
	 * @returns true if the specified target has an own property of the specified name
	 */
	hasOwn( target: unknown, key: string ): boolean;

	/**
	 * Escapes the characters "&", "<", ">", '"' and "'" in the specified target and returns the escaped target.
	 *
	 * @param target
	 * @returns escaped target
	 */
	escape( target: unknown ): string;
}
