/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "../../../util/lang/each";
import { PlainObject } from "../../../util/lang/plain-object";
import { Comparator, defaultComparator } from "../../internal/comparator";
import { AddedMapItems, RemovedMapItems, UpdatedMapItems } from "../s-map";
import { SMapEntry } from "../s-map-entry";
import { SContainer } from "./s-container";
import { SMapBaseMemory } from "./s-map-base-memory";
import { SMapBasePatch } from "./s-map-base-patch";
import { SMapBasePatches } from "./s-map-base-patches";

/**
 * Represents a map.
 */
export class SMapBase<V, P extends SMapBasePatch<V>,
	PS extends SMapBasePatches<V, P>, M extends SMapBaseMemory<V, P, PS>>
	extends SContainer<PlainObject<V | null>, P, PS, M> {

	static Entry: typeof SMapEntry = SMapEntry;

	constructor( memory: M ) {
		super( memory );
	}

	/**
	 * Returns the size of this map.
	 *
	 * @returns the size of this map
	 */
	size(): number {
		return this.__mem__.size_();
	}

	/**
	 * Returns true if this map is empty.
	 *
	 * @returns true if this map is empty
	 */
	isEmpty(): boolean {
		return this.__mem__.isEmpty_();
	}

	/**
	 * Returns true if the specified key is in this map.
	 *
	 * @param key the key to be tested
	 * @returns true if the specified key is in this map
	 */
	containsKey( key: string ): boolean {
		return this.__mem__.containsKey_( key );
	}

	/**
	 * Returns true if the specified value is in this map.
	 *
	 * @param element the value to be tested
	 * @param comparator comparator
	 * @param thisArg context for the comparator
	 * @returns true if the specified value is in this map.
	 */
	containsValue( element: unknown, comparator: Comparator= defaultComparator, thisArg: unknown= this ): boolean {
		return this.__mem__.containsValue_( element, comparator, thisArg );
	}

	/**
	 * Iterates over elements of this map, calling the iteratee for each element.
	 * The iteratee is bound to the thisArg and invoked with three arguments: value, key and this map.
	 * The iteratee may exit iteration early by explicitly returning false.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @returns this
	 */
	each( iteratee: Iteratee<string, V | null, this>, thisArg: unknown= this ): this {
		this.__mem__.each_( iteratee, thisArg );
		return this;
	}

	/**
	 * Iterates elements of this map, calling the iteratee for each element,
	 * removing all elements the iteratee does not return true.
	 * The iteratee is bound to the thisArg and invoked with three arguments: value, key and this map.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @throws Error if this is read-only
	 * @returns this
	 */
	filter( iteratee: Iteratee<string, V | null, this>, thisArg: unknown= this ): this {
		this.__mem__.filter_( iteratee, thisArg );
		return this;
	}

	/**
	 * Returns the first field value which the specified predicate returns true, or null if there is no such field.
	 * The predicate is bound to the thisArg and invoked with three arguments: field value, field name and this map.
	 *
	 * @param predicate the function called per iteration
	 * @param thisArg the this binding of the predicate
	 * @returns the first field value which the specified predicate returns true, or null if there is no such field.
	 */
	find( predicate: Iteratee<string, any, this>, thisArg: unknown= this ): unknown {
		return this.__mem__.find_( predicate, thisArg );
	}

	/**
	 * Returns the array of the values in this map.
	 *
	 * @returns the array of the values in this map
	 */
	values(): Array<V | null> {
		return this.__mem__.values_();
	}

	/**
	 * Returns the value of the specified key.
	 * Returns null if this map does not contains the specified key.
	 *
	 * @param key the key of the value
	 * @returns the value of the specified key
	 */
	get( key: string ): V | null {
		return this.__mem__.get_( key );
	}

	/**
	 * Associates the specified value with the specified key.
	 * If this map contained the specified key previously, the old value is replaced by the specified value.
	 *
	 * @param key the key with which the specified value is to be associated
	 * @param element the value to be associated with the specified key
	 * @returns the old value or null if this map did not contain the specified key previously
	 * @throws Error if this is read-only
	 * @throws Error if the specified key is null
	 * @throws Error if the specified value is null and this does not support a null value
	 */
	put( key: string, element: V | null ): unknown {
		return this.__mem__.put_( key, element );
	}

	/**
	 * Reassociates the value of the specified key.
	 *
	 * @param key the key which is to be reassociated
	 * @returns the value of the specified key or null if this map did not contain the specified key previously
	 * @throws Error if this is read-only
	 * @throws Error if the specified key is null
	 */
	reput( key: string ): V | null {
		return this.__mem__.reput_( key );
	}

	/**
	 * Copies all the mappings of the specified mapping to this map.
	 *
	 * @param mappings mappings to be stored in this map
	 * @throws Error if this is read-only
	 * @throws Error if the specified mappings is null
	 * @throws Error if the specified mappings contains a null value and this does not support a null value
	 * @returns this
	 */
	putAll( mappings: PlainObject<V | null> ): this {
		this.__mem__.putAll_( mappings );
		return this;
	}

	/**
	 * Clear this map and then associates the specified value with the specified key.
	 *
	 * @param key the key with which the specified value is to be associated
	 * @param element the value to be associated with the specified key
	 * @returns null
	 * @throws Error if this is read-only
	 * @throws Error if the specified key is null
	 * @throws Error if the specified value is null and this does not support a null value
	 */
	clearAndPut( key: string, element: V | null ): null {
		return this.__mem__.clearAndPut_( key, element );
	}

	/**
	 * Clear this map and then copies all the mappings of the specified mapping to this map.
	 *
	 * @param mappings mappings to be stored in this map
	 * @throws Error if this is read-only
	 * @throws Error if the specified mappings is null
	 * @throws Error if the specified mappings contains a null value and this does not support a null value
	 * @returns this
	 */
	clearAndPutAll( mappings: PlainObject<V | null> ): this {
		this.__mem__.clearAndPutAll_( mappings );
		return this;
	}

	/**
	 * Removes the mapping of the specified key.
	 *
	 * @param key the key of the mapping to be removed
	 * @returns the old value or null if this map did not contain the specified key
	 * @throws Error if this is read-only
	 */
	remove( key: string ): V | null {
		return this.__mem__.remove_( key );
	}

	/**
	 * Removes all of the mappings in this map.
	 *
	 * @throws Error if this is read-only
	 * @returns this
	 */
	clear(): this {
		this.__mem__.clear_();
		return this;
	}

	/**
	 * Returns the JSON representation of this map.
	 * Must not change the returned JSON.
	 *
	 * @returns the JSON representation of this map
	 */
	toJson(): PlainObject<V | null> {
		return this.__mem__.toJson_();
	}

	/**
	 * Sets to the specified JSON object.
	 *
	 * @param json JSON object to be set
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

	/**
	 * Returns the object representation of this map.
	 * Must not change the returned object.
	 *
	 * @returns the object representation of this map
	 */
	toObject(): PlainObject<V | null> {
		return this.__mem__.toObject_();
	}

	/**
	 * Triggered when a map is initialized or changed.
	 * If a map is initialized when event handlers are set,
	 * event handlers are invoked immediately.
	 *
	 * @event value
	 * @param event event object.
	 * @param addedItems mappings from keys to values of added items
	 * @param removedItems mappings from keys to values of removed items
	 * @param updatedItems mappings from keys to values of updated items
	 * @internal
	 */
	onvalue?(
		event: Event, addedItems: AddedMapItems<V>,
		removedItems: RemovedMapItems<V>, updatedItems: UpdatedMapItems<V>
	): void;

	/**
	 * Triggered when items are changed.
	 *
	 * @event change
	 * @param event Event object.
	 * @param addedItems mappings from keys to values of added items
	 * @param removedItems mappings from keys to values of removed items
	 * @param updatedItems mappings from keys to values of updated items
	 * @internal
	 */
	onchange?(
		event: Event, addedItems: AddedMapItems<V>,
		removedItems: RemovedMapItems<V>, updatedItems: UpdatedMapItems<V>
	): void;
}
