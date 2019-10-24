/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "../../util/lang/each";
import { isBoolean } from "../../util/lang/is-boolean";
import { PlainObject } from "../../util/lang/plain-object";
import { Comparator, defaultComparator } from "../internal/comparator";
import { SNavigableMapSubMapMemory } from "./internal/s-navigable-map-sub-map-memory";
import { SNavigableMapSubMapTarget } from "./internal/s-navigable-map-sub-map-target";
import { View } from "./internal/view";
import { AddedMapItems, RemovedMapItems, UpdatedMapItems } from "./s-map";
import { SMapEntry } from "./s-map-entry";

/**
 * A view of a navigable map.
 */
export class SNavigableMapSubMap<V>
	extends View<SNavigableMapSubMapMemory<V, SNavigableMapSubMap<V>>,
	SNavigableMapSubMap<V>, SNavigableMapSubMapTarget<V>> {
	constructor( memory: SNavigableMapSubMapMemory<V, SNavigableMapSubMap<V>> ) {
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
	containsValue( value: unknown, comparator: Comparator= defaultComparator, thisArg: unknown= this ): boolean {
		return this.__mem__.containsValue_( value, comparator, thisArg );
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
	each( iteratee: Iteratee<string, V | null, this>, thisArg: unknown= this, reverse= false ): this {
		this.__mem__.each_( iteratee, thisArg, reverse );
		return this;
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
	 * Iterates elements of this map, calling the iteratee for each element,
	 * removing all elements the iteratee does not return true.
	 * The iteratee is bound to the thisArg and invoked with three arguments: value, key and this map.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @throws Error if this is read-only
	 * @returns this
	 */
	filter( iteratee: Iteratee<string , V | null, this>, thisArg: unknown= this ): this {
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
	find( predicate: Iteratee<string, V | null, this>, thisArg: unknown= this, reverse= false ): unknown {
		return this.__mem__.find_( predicate, thisArg, reverse );
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
	 * Returns the lowest key in this map.
	 *
	 * @returns the lowest key in this map
	 * @throws Error if this map is empty
	 */
	firstKey(): string {
		return this.__mem__.firstKey_();
	}

	/**
	 * Returns the highest key in this map.
	 *
	 * @returns the highest key in this map
	 * @throws Error if this map is empty
	 */
	lastKey(): string {
		return this.__mem__.lastKey_();
	}

	/**
	 * Returns a key-value mapping of the lowest key in this map, or null if this map is empty.
	 *
	 * @returns a key-value mapping of the lowest key in this map, or null if this map is empty
	 */
	firstEntry(): SMapEntry<V> | null {
		return this.__mem__.firstEntry_();
	}

	/**
	 * Returns a key-value mapping of the highest key in this map, or null if this map is empty.
	 *
	 * @returns a key-value mapping of the highest key in this map, or null if this map is empty
	 */
	lastEntry(): SMapEntry<V> | null {
		return this.__mem__.lastEntry_();
	}

	/**
	 * Returns a key-value mapping of the highest key lower than or equal to the specified key,
	 * or null if there is no such key.
	 *
	 * @param key a key
	 * @returns a key-value mapping of the highest key lower than or equal to the specified key,
	 * or null if there is no such key
	 */
	floorEntry( key: string ): SMapEntry<V> | null {
		return this.__mem__.floorEntry_( key );
	}

	/**
	 * Returns a key-value mapping of the lowest key higher than or equal to the specified key,
	 * or null if there is no such key.
	 *
	 * @param key a key
	 * @returns a key-value mapping of the lowest key higher than or equal to the specified key,
	 * or null if there is no such key
	 */
	ceilingEntry( key: string ): SMapEntry<V> | null {
		return this.__mem__.ceilingEntry_( key );
	}

	/**
	 * Returns the highest key lower than or equal to the specified key, or null if there is no such key.
	 *
	 * @param key a key
	 * @returns the highest key lower than or equal to the specified key, or null if there is no such key.
	 */
	floorKey( key: string ): string | null {
		return this.__mem__.floorKey_( key );
	}

	/**
	 * Returns the lowest key higher than or equal to the specified key, or null if there is no such key.
	 *
	 * @param key a key
	 * @returns the lowest key higher than or equal to the specified key, or null if there is no such key
	 */
	ceilingKey( key: string ): string | null {
		return this.__mem__.ceilingKey_( key );
	}

	/**
	 * Returns a key-value mapping of the highest key lower than the specified key,
	 * or null if there is no such key.
	 *
	 * @param key a key
	 * @returns a key-value mapping of the highest key lower than the specified key,
	 * or null if there is no such key
	 */
	lowerEntry( key: string ): SMapEntry<V> | null {
		return this.__mem__.lowerEntry_( key );
	}

	/**
	 * Returns a key-value mapping of the lowest key higher than the specified key,
	 * or null if there is no such key.
	 *
	 * @param key a key
	 * @returns a key-value mapping of the lowest key higher than the specified key,
	 * or null if there is no such key
	 */
	higherEntry( key: string ): SMapEntry<V> | null {
		return this.__mem__.higherEntry_( key );
	}

	/**
	 * Returns the highest key lower than the specified key, or null if there is no such key.
	 *
	 * @param key a key
	 * @returns the highest key lower than the specified key, or null if there is no such key.
	 */
	lowerKey( key: string ): string | null {
		return this.__mem__.lowerKey_( key );
	}

	/**
	 * Returns the lowest key higher than the specified key, or null if there is no such key.
	 *
	 * @param key a key
	 * @returns the lowest key higher than the specified key, or null if there is no such key
	 */
	higherKey( key: string ): string | null {
		return this.__mem__.higherKey_( key );
	}

	/**
	 * Returns a view of the portion of this map whose keys are lower than (or equal to,
	 * if inclusive is true) the specified key `toKey`.
	 *
	 * @param toKey high endpoint of the keys in the view
	 * @param inclusive true if the specified key `toKey` is to be included in the view
	 * @returns a view of the specified portion of this map
	 * @throws Error `toKey` lies outside the bounds of this view
	 */
	headMap( toKey: string, inclusive= false ): SNavigableMapSubMap<V> {
		return this.__mem__.headMap_( toKey, inclusive ).getWrapper_();
	}

	/**
	 * Returns a view of the portion of this map whose keys are higher than (or equal to,
	 * if inclusive is true) the specified key `fromKey`.
	 *
	 * @param fromKey low endpoint of the keys in the view
	 * @param inclusive true if the specified key `fromKey` is to be included in the view
	 * @returns a view of the specified portion of this map
	 * @throws Error `fromKey` lies outside the bounds of this view
	 */
	tailMap( fromKey: string, inclusive= true ): SNavigableMapSubMap<V> {
		return this.__mem__.tailMap_( fromKey, inclusive ).getWrapper_();
	}

	/**
	 * Returns a view of the portion of this map whose keys range from `fromKey` to `toKey`.
	 *
	 * @param fromKey low endpoint of the keys in the view
	 * @param toKey high endpoint of the keys in the view
	 * @returns a view of the specified portion of this map
	 * @throws Error if `fromKey` is higher than `toKey`
	 * @throws Error `fromKey` or `toKey` lies outside the bounds of this view
	 */
	subMap( fromKey: string, toKey: string ): SNavigableMapSubMap<V>;

	/**
	 * Returns a view of the portion of this map whose keys range from `fromKey` to `toKey`.
	 *
	 * @param fromKey low endpoint of the keys in the view
	 * @param includeFrom true if the low endpoint is to be included in the view
	 * @param toKey high endpoint of the keys in the view
	 * @param includeTo true if the high endpoint is to be included in the view
	 * @returns a view of the specified portion of this map
	 * @throws Error if `fromKey` is higher than `toKey`
	 * @throws Error `fromKey` or `toKey` lies outside the bounds of this view
	 */
	subMap( fromKey: string, includeFrom: boolean, toKey: string, includeTo: boolean ): SNavigableMapSubMap<V>;

	subMap(
		fromKey: string, includeFromOrToKey: boolean|string, toKeyOrIncludeTo?: boolean|string,
		includeTo?: boolean
	): SNavigableMapSubMap<V> {
		let includeFrom = true;
		let toKey: string | null = null;
		if( arguments.length <= 2 ) {
			toKey = includeFromOrToKey as string;
			includeTo = false;
		} else if( arguments.length === 3 ) {
			if( isBoolean( toKeyOrIncludeTo ) ) {
				includeTo = toKeyOrIncludeTo;
				toKey = includeFromOrToKey as string;
			} else {
				includeTo = includeFromOrToKey as boolean;
				toKey = toKeyOrIncludeTo as string;
			}
		} else {
			includeFrom = includeFromOrToKey as boolean;
			toKey = toKeyOrIncludeTo as string;
		}

		if( includeFrom == null ) {
			includeFrom = true;
		}

		if( includeTo == null ) {
			includeTo = false;
		}

		return this.__mem__.subMap_( fromKey, includeFrom, toKey, includeTo ).getWrapper_();
	}

	/**
	 * Returns a reverse-order view of this map.
	 *
	 * @returns reverse-order view of this map
	 */
	descendingMap(): SNavigableMapSubMap<V> {
		return this.__mem__.descendingMap_().getWrapper_();
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
	 * @throws Error the specified key lies outside the bounds of this view
	 */
	put( key: string, value: V | null ): V | null {
		return this.__mem__.put_( key, value );
	}

	/**
	 * Reassociates the value of the specified key.
	 *
	 * @param key the key which is to be reassociated
	 * @returns the value of the specified key or null if this map did not contain the specified key previously
	 * @throws Error if this is read-only
	 * @throws Error if the specified key is null
	 * @throws Error the specified key lies outside the bounds of this view
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
	 * @throws Error the specified key lies outside the bounds of this view
	 * @returns this
	 */
	putAll( mappings: PlainObject<V | null> ): this {
		this.__mem__.putAll_( mappings );
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
		return this.__mem__.toObject_();
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
	 * Returns the string representation of this container.
	 *
	 * @returns the string representation of this container
	 */
	toString(): string {
		return this.__mem__.toString_();
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
