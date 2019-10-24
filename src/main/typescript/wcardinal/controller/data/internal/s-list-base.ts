/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "../../../util/lang/each";
import { Comparator, defaultComparator } from "../../internal/comparator";
import { SContainer } from "./s-container";
import { SListBaseMemory } from "./s-list-base-memory";
import { SListBasePatch } from "./s-list-base-patch";
import { SListBasePatches } from "./s-list-base-patches";

export class SListBase<V, P extends SListBasePatch<V>,
	PS extends SListBasePatches<V, P>, M extends SListBaseMemory<V, P, PS>>
	extends SContainer<Array<V | null>, P, PS, M> {
	constructor( memory: M ) {
		super( memory );
	}

	/**
	 * Returns the size of this list.
	 *
	 * @returns the size of this list
	 */
	size(): number {
		return this.__mem__.size_();
	}

	/**
	 * Returns true if this list is empty.
	 *
	 * @returns true if this list is empty
	 */
	isEmpty(): boolean {
		return this.__mem__.isEmpty_();
	}

	/**
	 * Returns the index of the specified value.
	 *
	 * @param value value to search for
	 * @param comparator
	 * @param thisArg context for the comparator
	 * @returns the index of the specified value or -1 if this list does not contain the specified value
	 */
	indexOf( value: V | null, comparator: Comparator= defaultComparator, thisArg: unknown= this ): number {
		return this.__mem__.indexOf_( value, comparator, thisArg );
	}

	/**
	 * Returns the last index of the specified value.
	 *
	 * @param value value to search for
	 * @param comparator comparator
	 * @param thisArg context for the comparator
	 * @returns the last index of the specified value or -1 if this list does not contain the specified value
	 */
	lastIndexOf( value: V | null, comparator: Comparator= defaultComparator, thisArg: unknown= this ): number {
		return this.__mem__.lastIndexOf_( value, comparator, thisArg );
	}

	/**
	 * Returns true if this list contains the specified value.
	 *
	 * @param value value to search for
	 * @param comparator comparator
	 * @param thisArg context for the comparator
	 * @returns true if this list contains the specified value
	 */
	contains( value: V | null, comparator: Comparator= defaultComparator, thisArg: unknown= this ): boolean {
		return this.__mem__.contains_( value, comparator, thisArg );
	}

	/**
	 * Returns true if this list contains all the specified values.
	 *
	 * @param values values to search for
	 * @param comparator comparator
	 * @param thisArg context for the comparator
	 * @returns true if this list contains all the specified values
	 */
	containsAll(
		values: ArrayLike<V | null>, comparator: Comparator= defaultComparator, thisArg: unknown= this
	): boolean {
		return this.__mem__.containsAll_( values, comparator, thisArg );
	}

	/**
	 * Iterates over elements of this list, calling the iteratee for each element.
	 * The iteratee is bound to the thisArg and invoked with three arguments: value, index and this list.
	 * The iteratee may exit iteration early by explicitly returning false.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @param reverse true to iterate in the reverse order
	 * @returns this
	 */
	each( iteratee: Iteratee<number, V | null, this>, thisArg: unknown= this, reverse= false ): this {
		this.__mem__.each_( iteratee, thisArg, reverse );
		return this;
	}

	/**
	 * Iterates elements of this list, calling the iteratee for each element,
	 * removing all elements the iteratee does not return true.
	 * The iteratee is bound to the thisArg and invoked with three arguments: value, key and this list.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @throws Error if this is read-only
	 * @returns this
	 */
	filter( iteratee: Iteratee<number, V | null, this>, thisArg: unknown= this ): this {
		this.__mem__.filter_( iteratee, thisArg );
		return this;
	}

	/**
	 * Returns the first element which the specified predicate returns true,
	 * or null if there is no such element.
	 * The predicate is bound to the thisArg and invoked with three arguments: value, index and this list.
	 *
	 * @param predicate the function called per iteration
	 * @param thisArg the this binding of the predicate
	 * @param reverse true to iterate in the reverse order
	 * @returns the first element which the predicate returns true, or null if there is no such element
	 */
	find( predicate: Iteratee<number, V | null, this>, thisArg: unknown= this, reverse= false ): unknown {
		return this.__mem__.find_( predicate, thisArg, reverse );
	}

	/**
	 * Returns the value at the specified index.
	 *
	 * @param index the index of the value
	 * @returns the value at the specified index
	 */
	get( index: number ): unknown {
		return this.__mem__.get_( index );
	}

	/**
	 * Replaces the value at the specified index by the specified value.
	 *
	 * @param index an index
	 * @param value a value
	 * @returns the old value
	 * @throws Error if this is read-only
	 * @throws Error if the specified value is null and this does not support a null value
	 * @throws Error if the index is null
	 * @throws Error if the index is out of bounds (index < 0 || size() <= index)
	 */
	set( index: number, value: V | null ): V | null {
		return this.__mem__.set_( index, value );
	}

	/**
	 * Resets the value at the specified index.
	 *
	 * @param index the index of the value
	 * @returns the value at the specified index
	 * @throws Error if this is read-only
	 * @throws Error if the index is null
	 * @throws Error if the index is out of bounds (index < 0 || size() <= index)
	 */
	reset( index: number ): unknown {
		return this.__mem__.reset_( index );
	}

	/**
	 * Inserts the specified value.
	 *
	 * @param value the value to be inserted
	 * @throws Error if this is read-only
	 * @throws Error if the specified value is null and this does not support a null value
	 * @returns true
	 */
	add( value: V | null ): boolean;

	/**
	 * Inserts the specified value at the specified index.
	 *
	 * @param index the index where the specified value to be inserted
	 * @param value the value to be inserted
	 * @throws Error if this is read-only
	 * @throws Error if the specified value is null and this does not support a null value
	 * @throws Error if the index is out of bounds (index < 0 || size() < index)
	 * @returns true
	 */
	add( index: number, value: V | null ): boolean;

	add( indexOrValue: unknown, value?: V | null ): boolean {
		const m = this.__mem__;
		if( arguments.length <= 1 ) {
			return m.add_( m.size_(), indexOrValue as (V | null) );
		} else {
			return m.add_( indexOrValue as number, value! );
		}
	}

	/**
	 * Inserts all the elements of the specified array.
	 *
	 * @param values the array of elements to be inserted
	 * @throws Error if this is read-only
	 * @throws Error if the specified array is null
	 * @throws Error if the specified array contains null and this does not support a null value
	 * @returns {boolean} true if this list is changed as a result of the call
	 */
	addAll( values: ArrayLike<V | null> ): boolean;

	/**
	 * Inserts all the elements of the specified array at the specified index of this list.
	 *
	 * @param index the index where the specified values are inserted
	 * @param values the array of elements to be inserted
	 * @throws Error if this is read-only
	 * @throws Error if the specified array is null
	 * @throws Error if the specified array contains null and this does not support a null value
	 * @returns {boolean} true if this list is changed as a result of the call
	 */
	addAll( index: number, values: ArrayLike<V | null> ): boolean;

	addAll( indexOrValues: number|ArrayLike<V | null>, values?: ArrayLike<V | null> ): boolean {
		const m = this.__mem__;
		if( arguments.length <= 1 ) {
			return m.addAll_( m.size_(), indexOrValues as ArrayLike<V | null> );
		} else {
			return m.addAll_( indexOrValues as number, values! );
		}
	}

	/**
	 * Clear this list and then appends the specified value at the end of this list.
	 *
	 * @param value the value to be inserted
	 * @throws Error if this is read-only
	 * @throws Error if the specified value is null and this does not support a null value
	 * @returns true
	 */
	clearAndAdd( value: V | null ): boolean {
		return this.__mem__.clearAndAdd_( value );
	}

	/**
	 * Clear this list and then appends all of the elements in the specified array to the end of this list.
	 *
	 * @param values the array of elements to be appended
	 * @throws Error if this is read-only
	 * @throws Error if the specified array is null
	 * @throws Error if the specified array contains null and this does not support a null value
	 * @returns true if this list is changed as a result of the call
	 */
	clearAndAddAll( values: ArrayLike<V | null> ): boolean {
		return this.__mem__.clearAndAddAll_( values );
	}

	/**
	 * Removes the value at the specified index.
	 *
	 * @param index index of the element to be removed
	 * @returns the removed element at the specified index
	 * @throws Error if this is read-only
	 * @throws Error if the index is out of bounds (index < 0 || size() <= index)
	 */
	remove( index: number ): V | null {
		return this.__mem__.remove_( index );
	}

	/**
	 * Removes all of the elements in this list.
	 *
	 * @throws Error if this is read-only
	 * @returns this
	 */
	clear(): this {
		this.__mem__.clear_();
		return this;
	}

	/**
	 * Returns the array of the object representations of elements.
	 * Each object has an index and a value.
	 *
	 * @returns the array of the object representations of elements
	 */
	toArrayObject() {
		return this.__mem__.toArrayObject_();
	}

	/**
	 * Returns the array representation of this list.
	 * Must not change the returned array.
	 *
	 * @returns the array representation of this list
	 */
	toArray(): Array<V | null> {
		return this.__mem__.toArray_();
	}

	toJson(): Array<V | null> {
		return this.__mem__.toJson_();
	}

	/**
	 * Sets to the specified JSON array.
	 *
	 * @param json JSON array to be set
	 * @returns this
	 */
	fromJson( json: unknown ): this {
		this.__mem__.fromJson_( json );
		return this;
	}

	/**
	 * Sets to the specified string.
	 *
	 * @param str string to be set
	 * @returns this
	 */
	fromString( str: string ): this {
		this.__mem__.fromString_( str );
		return this;
	}
}
