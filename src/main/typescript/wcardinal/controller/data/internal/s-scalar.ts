/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../../../event/connectable";
import { Iteratee } from "../../../util/lang/each";
import { Comparator, defaultComparator } from "../../internal/comparator";
import { SScalarMemory } from "./s-scalar-memory";

export const toSScalarValue = <V = unknown>( value: V | null | SScalar<V> ): V | null => {
	return ( value instanceof SScalar ? value.get() : value );
};

/**
 * Represents a scalar data.
 * The scalar data this class has is to be synchronized with the one on a server.
 */
export class SScalar<V = unknown, M extends SScalarMemory<V> = SScalarMemory<V>> extends Connectable {
	constructor( protected readonly __mem__: M ) {
		super();
	}

	/**
	 * Returns true if this is read-only.
	 *
	 * @returns true if this is read-only
	 */
	isReadOnly(): boolean {
		return this.__mem__.isReadOnly_();
	}

	/**
	 * Returns true if this is non-null.
	 *
	 * @returns true if this is non-null.
	 */
	isNonNull(): boolean {
		return this.__mem__.isNonNull_();
	}

	uninitialize(): this {
		this.__mem__.uninitialize_();
		return this;
	}

	/**
	 * Returns true if this is initialized.
	 *
	 * @returns true if this is initialized.
	 */
	isInitialized(): boolean {
		return this.__mem__.isInitialized_();
	}

	initialize(): this {
		this.__mem__.initialize_();
		return this;
	}

	/**
	 * Locks this data.
	 *
	 * @returns this
	 */
	lock(): this {
		this.__mem__.lock_();
		return this;
	}

	/**
	 * Returns true if this data is locked.
	 *
	 * @returns true if this data is locked
	 */
	isLocked(): boolean {
		return this.__mem__.isLocked_();
	}

	/**
	 * Unlocks this data.
	 *
	 * @returns this
	 */
	unlock(): this {
		this.__mem__.unlock_();
		return this;
	}

	/**
	 * Marks as updated.
	 *
	 * @throws Error if this is read-only
	 * @returns this
	 */
	toDirty(): this {
		this.__mem__.toDirty_();
		return this;
	}

	/**
	 * Returns the current value.
	 *
	 * @returns the current value
	 */
	get(): V | null {
		return this.__mem__.get_();
	}

	/**
	 * Sets to the specified value if the specified value differs from the current value.
	 * If the `forcibly` is true, sets to the specified value even if the specified value equals to the current value.
	 *
	 * @param value new value
	 * @param forcibly true to ignore an equality check
	 * @throws Error if this is read-only
	 * @throws Error if `value` is null and this is non-null
	 * @returns this
	 */
	set( value: V | null, forcibly: boolean= false ): this {
		this.__mem__.set_( value, forcibly );
		return this;
	}

	/**
	 * Resets to the current value.
	 *
	 * @throws Error if this is read-only
	 * @returns the current value
	 */
	reset(): V | null {
		return this.__mem__.reset_();
	}

	/**
	 * Returns the current value.
	 *
	 * @returns the current value
	 */
	getValue(): V | null {
		return this.__mem__.get_();
	}

	/**
	 * Sets to the specified value if the specified value differs from the current value.
	 * If the `forcibly` is true, sets to the specified value even if the specified value equals to the current value.
	 *
	 * @param value the new value
	 * @param forcibly true to ignore an equality check
	 * @throws Error if this is read-only
	 * @throws Error if `value` is null and this is non-null
	 * @returns this
	 */
	setValue( value: V | null, forcibly: boolean= false ): this {
		this.__mem__.set_( value, forcibly );
		return this;
	}

	/**
	 * Resets to the current value.
	 *
	 * @throws Error if this is read-only
	 * @returns the current value
	 */
	resetValue(): V | null {
		return this.__mem__.reset_();
	}

	/**
	 * Sets to the specified value and returns the old value if the specified value differs from the current value.
	 * If the `forcibly` is true, sets to the specified value even if the specified value equals to the current value.
	 *
	 * @param value the new value
	 * @param forcibly true to ignore an equality check
	 * @returns the old value
	 * @throws Error if this is read-only
	 * @throws Error if `value` is null and this is non-null
	 */
	getAndSet( value: V | null, forcibly: boolean= false ): V | null {
		return this.__mem__.set_( value, forcibly );
	}

	/**
	 * Sets to the `update` if the `expected` equals to this value and the `update` differs from the current value.
	 * If the `forcibly` is true, sets to the specified value even if the `update` equals to the current value.
	 *
	 * @param expected the expected value
	 * @param update the new value
	 * @param forcibly true to ignore an equality check
	 * @returns true if the `expected` is equal to this value
	 * @throws Error if this is read-only
	 * @throws Error if `update` is null and this is non-null
	 */
	compareAndSet( expected: V | null, update: V | null, forcibly: boolean= false ): boolean {
		return this.__mem__.compareAndSet_( expected, update, forcibly );
	}

	/**
	 * Returns true if this value is equal to the specified value.
	 *
	 * @param value value to be compared
	 * @returns true if this value is equal to the specified value
	 */
	equals( value: V | null | SScalar<V> ): boolean {
		return this.__mem__.equals_( toSScalarValue( value ) );
	}

	/**
	 * Compares this value with the specified value.
	 * Returns a negative integer, zero, a positive integer as this value is less than,
	 * equals to, or greater than the specified value.
	 *
	 * @param value value to be compared
	 * @returns a negative integer, zero, a positive integer as this value is less than,
	 * equals to, or greater than the specified value
	 */
	compareTo( value: V | null | SScalar<V> ): number {
		return this.__mem__.compareTo_( toSScalarValue( value ) );
	}

	/**
	 * If the value is an array, returns the size of the array.
	 *
	 * If the value is an object, returns the number of fields in the object.
	 *
	 * If the value is null/undefined, returns 0.
	 *
	 * For anything else, returns 1.
	 *
	 * @returns the size of this value
	 */
	size(): number {
		return this.__mem__.size_();
	}

	/**
	 * Returns true if the size is zero.
	 *
	 * @returns true if the size is zero
	 */
	isEmpty(): boolean {
		return this.__mem__.isEmpty_();
	}

	/**
	 * Returns true if the value is null/undefined.
	 *
	 * @returns true if the value is null/undefined.
	 */
	isNull(): boolean {
		return this.__mem__.isNull_();
	}

	/**
	 * Returns true if the value is not null/undefined.
	 *
	 * @returns true if the value is not null/undefined.
	 */
	isNotNull(): boolean {
		return this.__mem__.isNotNull_();
	}

	/**
	 * If the value is an object/an array, iterates over fields/elements of the value,
	 * calling the iteratee for each field/element.
	 * The iteratee is invoked with three arguments: field value/element, field name/index and this instance.
	 *
	 * If the value is null/undefined, does not invoke the iteratee.
	 *
	 * For anything else, invokes the iteratee with three arguments: the value, null and this instance.
	 *
	 * The iteratee is bound to the specified thisArg and may exit iteration early by explicitly returning false.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @param reverse true to iterate in the reverse order if the value is an array
	 * @returns this
	 */
	each( iteratee: Iteratee<string, any, this>, thisArg?: SScalarMemory<V>, reverse?: boolean ): this;

	/**
	 * If the value is an object/an array, iterates over fields/elements of the value,
	 * calling the iteratee for each field/element.
	 * The iteratee is invoked with three arguments: field value/element, field name/index and this instance.
	 *
	 * If the value is null/undefined, does not invoke the iteratee.
	 *
	 * For anything else, invokes the iteratee with three arguments: the value, null and this instance.
	 *
	 * The iteratee is bound to the specified thisArg and may exit iteration early by explicitly returning false.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @param reverse true to iterate in the reverse order if the value is an array
	 * @returns this
	 */
	each( iteratee: Iteratee<number, any, this>, thisArg?: SScalarMemory<V>, reverse?: boolean ): this;

	each(
		iteratee: Iteratee<any, any, this>,
		thisArg: unknown= this,
		reverse= false
	): this {
		this.__mem__.each_( iteratee, thisArg, reverse );
		return this;
	}

	/**
	 * If the value is an object/an array, returns the first field/element
	 * which the specified predicate returns true or null if there is no such field/element.
	 * The predicate is bound to the thisArg and invoked with three arguments: value,
	 * field name/index and this instance.
	 *
	 * If the value is null/undefined, does not invokes the predicate and returns null.
	 *
	 * For anything else, invokes the predicate with three arguments: the value, null and this instance,
	 * and returns the value if the predicate returns true.
	 *
	 * @param predicate the function called per iteration
	 * @param thisArg the this binding of the predicate
	 * @param reverse true to iterate in the reverse order if the value is an array
	 * @returns the found value or null
	 */
	find( predicate: Iteratee<string, any, this>, thisArg?: unknown, reverse?: boolean ): unknown;

	/**
	 * If the value is an object/an array, returns the first field/element
	 * which the specified predicate returns true or null if there is no such field/element.
	 * The predicate is bound to the thisArg and invoked with three arguments: value,
	 * field name/index and this instance.
	 *
	 * If the value is null/undefined, does not invokes the predicate and returns null.
	 *
	 * For anything else, invokes the predicate with three arguments: the value, null and this instance,
	 * and returns the value if the predicate returns true.
	 *
	 * @param predicate the function called per iteration
	 * @param thisArg the this binding of the predicate
	 * @param reverse true to iterate in the reverse order if the value is an array
	 * @returns the found value or null
	 */
	find( predicate: Iteratee<number, any, this>, thisArg?: unknown, reverse?: boolean ): unknown;

	find(
		predicate: Iteratee<any, any, this>,
		thisArg: unknown= this,
		reverse= false
	): unknown {
		return this.__mem__.find_( predicate, thisArg, reverse );
	}

	/**
	 * If the value is an array, returns the index of the specified value.
	 * Otherwise, returns -1.
	 *
	 * @param value value to search for
	 * @param comparator comparator
	 * @param thisArg context for the comparator
	 * @returns the index of the specified value
	 */
	indexOf( value: unknown, comparator: Comparator= defaultComparator, thisArg: unknown= this ): number {
		return this.__mem__.indexOf_( value, comparator, thisArg );
	}

	/**
	 * If the value is an array, returns the last index of the specified value.
	 * Otherwise, returns -1.
	 *
	 * @param value value to search for
	 * @param comparator comparator
	 * @param thisArg context for the comparator
	 * @returns the last index of the specified value
	 */
	lastIndexOf( value: unknown, comparator: Comparator= defaultComparator, thisArg: unknown= this ): number {
		return this.__mem__.lastIndexOf_( value, comparator, thisArg );
	}

	/**
	 * Returns true if this contains the specified value.
	 *
	 * @param value value to search for
	 * @param comparator comparator
	 * @param thisArg context for the comparator
	 * @returns true if this contains the specified value
	 */
	contains( value: unknown, comparator: Comparator= defaultComparator, thisArg: unknown= this ): boolean {
		return this.__mem__.contains_( value, comparator, thisArg );
	}

	/**
	 * Returns true if this contains all the specified values.
	 *
	 * @param values values to search for
	 * @param comparator comparator
	 * @param thisArg context for the comparator
	 * @returns true if this contains all the specified values
	 */
	containsAll(
		values: ArrayLike<unknown>, comparator: Comparator= defaultComparator, thisArg: unknown= this
	): boolean {
		return this.__mem__.containsAll_( values, comparator, thisArg );
	}

	/**
	 * Returns the JSON representing this value.
	 * Must not change the returned value.
	 *
	 * @returns the JSON representing this value
	 */
	toJson(): unknown {
		return this.__mem__.toJson_();
	}

	/**
	 * Sets to the specified JSON.
	 *
	 * @param json JSON to be set
	 * @returns this
	 */
	fromJson( json: unknown ): this {
		this.__mem__.fromJson_( json );
		return this;
	}

	/**
	 * Returns the string representing this value.
	 *
	 * @returns the string representing this value
	 */
	toString(): string {
		return this.__mem__.toString_();
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

	/**
	 * Triggered when this scalar is initialized or changed.
	 * If this scalar is initialized when event handlers are set,
	 * event handlers are invoked immediately.
	 *
	 *     on( "value", ( event, newValue, oldValue ) => {
	 *         // DO SOMETHING HERE
	 *     })
	 *
	 * @event value
	 * @param event an event object
	 * @param newValue a new value
	 * @param oldValue an old value
	 * @internal
	 */
	onvalue?( event: Event, newValue: V | null, oldValue: V | null ): void;

	/**
	 * Triggered when the value is changed.
	 *
	 *     on( "change", ( event, newValue, oldValue ) => {
	 *         // DO SOMETHING HERE
	 *     })
	 *
	 * @event change
	 * @param event an event object
	 * @param newValue a new value
	 * @param oldValue an old value
	 * @internal
	 */
	onchange?( event: Event, newValue: V | null, oldValue: V | null ): void;
}
